package com.Synchrome.collabcontent.canvas.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CanvasCreateReqDto {
    private String title;
    private String ydocState;
    private Long userId;
    private Long channelId;
    private Long workspaceId;
}
