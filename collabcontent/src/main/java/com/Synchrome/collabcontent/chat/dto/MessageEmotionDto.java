package com.Synchrome.collabcontent.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageEmotionDto {
    private Long messageId;
    private List<EmotionDetailDto> emojis;
}
