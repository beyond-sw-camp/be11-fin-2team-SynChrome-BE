package com.Synchrome.collabcontent.canvas.service;

import com.Synchrome.collabcontent.canvas.domain.Canvas;
import com.Synchrome.collabcontent.canvas.domain.CanvasBlock;
import com.Synchrome.collabcontent.canvas.dto.CanvasCreateReqDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasListResDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasMessageDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasSaveReqDto;
import com.Synchrome.collabcontent.canvas.repository.CanvasBlockRepository;
import com.Synchrome.collabcontent.canvas.repository.CanvasRepository;
import com.Synchrome.collabcontent.common.redis.YDocManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CanvasService {
    private final CanvasRepository canvasRepository;
    private final CanvasBlockRepository canvasBlockRepository;
    private final YDocManager yDocManager;
    private final ObjectMapper objectMapper;

    public CanvasService(CanvasRepository canvasRepository, CanvasBlockRepository canvasBlockRepository, YDocManager yDocManager, ObjectMapper objectMapper) {
        this.canvasRepository = canvasRepository;
        this.canvasBlockRepository = canvasBlockRepository;
        this.yDocManager = yDocManager;
        this.objectMapper = objectMapper;
    }

    public void saveCanvas(CanvasMessageDto dto) {
        try {
            byte[] decodedUpdate = Base64.getDecoder().decode(dto.getUpdate());
            yDocManager.appendUpdate(dto.getCanvasId(), decodedUpdate);

            System.out.println("✅ Yjs update Redis에 저장 완료");

        } catch (Exception e) {
            System.err.println("❌ Canvas 저장 실패: " + e.getMessage());
        }
    }

    public void saveCanvasBlocks(CanvasSaveReqDto request) {
        Long canvasId = request.getCanvasId();

        // 기존 블록 삭제 (전체 덮어쓰기 방식)
        canvasBlockRepository.deleteAllByCanvasId(canvasId);

        // blocks가 null이면 빈 리스트로 처리
        List<CanvasBlock> blocks = Optional.ofNullable(request.getBlocks())
                .orElseGet(ArrayList::new)
                .stream()
                .map(dto -> {
                    String contentStr = dto.getContent().toString(); // JsonNode → String
                    return CanvasBlock.builder()
                            .canvasId(canvasId)
                            .type(dto.getType())
                            .orderKey(dto.getOrderKey())
                            .indent(dto.getIndent())
                            .content(contentStr)
                            .build();
                }).toList();

        canvasBlockRepository.saveAll(blocks);
    }

    public Map<String, Object> loadCanvasContent(Long canvasId) {
        // 1. 캔버스 title 조회
        Optional<Canvas> canvasOpt = canvasRepository.findById(canvasId);
        String title = canvasOpt.map(Canvas::getTitle).orElse("제목 없음");

        // 2. 블록 내용 조회
        List<CanvasBlock> blocks = canvasBlockRepository.findAllByCanvasIdOrderByOrderKeyAsc(canvasId);
        List<Map<String, Object>> contentList = blocks.stream().map(block -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", block.getType());
            node.put("attrs", Map.of(
                    "orderKey", block.getOrderKey(),
                    "indent", block.getIndent()
            ));

            try {
                JsonNode content = objectMapper.readTree(block.getContent());
                node.put("content", content);
            } catch (Exception e) {
                node.put("content", List.of()); // 파싱 실패 시 fallback
            }

            return node;
        }).toList();

        // 3. title + content 함께 반환
        return Map.of(
                "title", title,
                "type", "doc",
                "content", contentList
        );
    }


    public Long createCanvas(CanvasCreateReqDto canvasCreateReqDto) {
        Canvas canvas = Canvas.builder().title(canvasCreateReqDto.getTitle()).build();
        return canvasRepository.save(canvas).getId();
    }

    public List<CanvasListResDto> getCanvasList() {
        return canvasRepository.findAll().stream()
                .map(canvas -> new CanvasListResDto(
                        canvas.getId(),
                        canvas.getTitle()
                ))
                .toList();
    }
}
