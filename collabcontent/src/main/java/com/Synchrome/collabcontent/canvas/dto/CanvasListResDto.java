package com.Synchrome.collabcontent.canvas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CanvasListResDto {
    private Long id;
    private String title;
}
