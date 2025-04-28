package com.Synchrome.collabcontent.chat.dto;


import com.Synchrome.collabcontent.chat.domain.ChatMessage;
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
    private Long workspaceId;


    public static ChatMessageDto fromEntity(ChatMessage entity) {
        return ChatMessageDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .roomId(entity.getChatRoom().getId())
                .message(entity.getContent())
                .createdTime(entity.getCreatedTime())
                .parentId(entity.getParentId())
                .workspaceId(entity.getWorkspaceId())
                .build();
    }


}