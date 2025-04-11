package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

import java.util.List;

@Data
public class CanvasSaveReqDto {
    private Long canvasId;
    private List<CanvasBlockDto> blocks;
}
