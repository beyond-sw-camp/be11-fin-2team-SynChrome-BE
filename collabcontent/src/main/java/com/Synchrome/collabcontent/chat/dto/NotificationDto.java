package com.Synchrome.collabcontent.chat.dto;


import com.Synchrome.collabcontent.chat.domain.ENUM.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long userId;
    private Long fromUserId;
    private Long roomId;
    private String message;
    private LocalDateTime timestamp;
    private Long chatMessageId;
    private NotificationType type;
    private boolean read;
    private String workspaceTitle;
    private Long workspaceId;



}

