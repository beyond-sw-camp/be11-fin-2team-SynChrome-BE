package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CanvasUpdateReqDto {
    private Long canvasId;
    private String update; // base64 encoded Yjs update
}