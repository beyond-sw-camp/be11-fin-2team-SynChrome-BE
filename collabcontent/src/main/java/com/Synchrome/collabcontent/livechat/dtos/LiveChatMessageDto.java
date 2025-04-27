package com.Synchrome.collabcontent.livechat.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveChatMessageDto {
    private Long roomId;
    private String type;    // "LIVE_CHAT_STARTED"
    private String content;
}
