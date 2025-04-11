package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

@Data
public class CanvasMessageDto {
    private Long canvasId;
    private Long userId;
    private String update; // base64 encoded Yjs update
}