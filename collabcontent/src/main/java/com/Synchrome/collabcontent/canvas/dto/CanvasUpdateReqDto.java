package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CanvasUpdateReqDto {
    private Long canvasId;
    private String update;
    private String awareness;
}