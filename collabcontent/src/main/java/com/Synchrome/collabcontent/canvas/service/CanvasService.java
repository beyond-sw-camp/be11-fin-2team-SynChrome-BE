package com.Synchrome.collabcontent.canvas.service;

import com.Synchrome.collabcontent.canvas.domain.Canvas;
import com.Synchrome.collabcontent.canvas.dto.CanvasCreateReqDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasListResDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasSaveReqDto;
import com.Synchrome.collabcontent.canvas.repository.CanvasRepository;
import com.Synchrome.collabcontent.common.redis.YDocManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CanvasService {
    private final CanvasRepository canvasRepository;
    private final YDocManager yDocManager;
    private final ObjectMapper objectMapper;

    public CanvasService(CanvasRepository canvasRepository, YDocManager yDocManager, ObjectMapper objectMapper) {
        this.canvasRepository = canvasRepository;
        this.yDocManager = yDocManager;
        this.objectMapper = objectMapper;
    }


    public void saveCanvas(CanvasSaveReqDto canvasSaveReqDto) {
        Canvas canvas = canvasRepository.findById(canvasSaveReqDto.getCanvasId()).orElseThrow(() -> new IllegalArgumentException("없는 캔버스입니다"));
        canvas.updateYdocState(canvasSaveReqDto.getYdocState());
    }

    public Canvas loadCanvasContent(Long canvasId) {
        Canvas canvas =  canvasRepository.findById(canvasId).orElseThrow(() -> new IllegalArgumentException("없는 캔버스입니다"));
        return canvas;
    }

    public Long createCanvas(CanvasCreateReqDto canvasCreateReqDto) {
        Canvas canvas = Canvas.builder()
                .title(canvasCreateReqDto.getTitle())
                .ydocState(canvasCreateReqDto.getYdocState())
                .build();
        return canvasRepository.save(canvas).getId();
    }

    public List<CanvasListResDto> getCanvasList() {
        return canvasRepository.findAll().stream()
                .map(canvas -> new CanvasListResDto(
                        canvas.getId(),
                        canvas.getTitle(),
                        canvas.getUpdatedAt()
                ))
                .toList();
    }
}
