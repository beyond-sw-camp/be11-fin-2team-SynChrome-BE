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
public class NotificationDto {
    private Long userId;
    private Long fromUserId;
    private Long roomId;
    private String message;
    private LocalDateTime timestamp;
}
