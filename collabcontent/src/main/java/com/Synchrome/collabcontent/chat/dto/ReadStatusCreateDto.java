package com.Synchrome.collabcontent.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jmx.export.annotation.ManagedNotifications;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReadStatusCreateDto {
    private Long userId;
    private Long lastReadMessageId;
}
