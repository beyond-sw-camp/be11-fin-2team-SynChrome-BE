package com.Synchrome.collabcontent.canvas.service;

import com.Synchrome.collabcontent.canvas.domain.Canvas;
import com.Synchrome.collabcontent.canvas.dto.CanvasCreateReqDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasListResDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasSaveReqDto;
import com.Synchrome.collabcontent.canvas.repository.CanvasRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CanvasService {
    private final CanvasRepository canvasRepository;
    private final ObjectMapper objectMapper;

    public CanvasService(CanvasRepository canvasRepository, ObjectMapper objectMapper) {
        this.canvasRepository = canvasRepository;
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
                        canvas.getUpdatedTime(),
                        canvas.getCreatedTime(),
                        canvas.getChannelId(),
                        canvas.getWorkspaceId(),
                        canvas.getUserId()
                ))
                .toList();
    }

    public List<CanvasListResDto> getCanvasByChannelId(Long channelId) {
        return canvasRepository.findByChannelId(channelId)
                .stream()
                .map(canvas -> new CanvasListResDto(
                        canvas.getId(),
                        canvas.getTitle(),
                        canvas.getUpdatedTime(),
                        canvas.getCreatedTime(),
                        canvas.getChannelId(),
                        canvas.getWorkspaceId(),
                        canvas.getUserId()
                ))
                .toList();
    }
}
