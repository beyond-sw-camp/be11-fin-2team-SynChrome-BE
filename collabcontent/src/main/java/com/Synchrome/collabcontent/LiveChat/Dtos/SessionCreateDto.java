package com.Synchrome.collabcontent.LiveChat.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SessionCreateDto {
//    private Long channelId;
    private String sessionId;
//    private String participantId;
}
