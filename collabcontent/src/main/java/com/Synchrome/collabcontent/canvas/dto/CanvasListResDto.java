package com.Synchrome.collabcontent.canvas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CanvasListResDto {
    private Long id;
    private String title;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Long userId;
    private Long channelId;
    private Long workspaceId;
}
