package com.Synchrome.collabcontent.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmotionDetailDto {
    private String emoji;
    private List<String> userIds;
    private Long count;
}
