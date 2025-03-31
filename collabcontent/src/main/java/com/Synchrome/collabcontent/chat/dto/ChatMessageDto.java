package com.Synchrome.collabcontent.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private Long userId;
    private Long roomId;
    private String message;
    private LocalDateTime createdTime;
    private Long parentId;

}