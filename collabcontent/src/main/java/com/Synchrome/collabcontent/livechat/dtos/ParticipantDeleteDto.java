package com.Synchrome.collabcontent.livechat.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ParticipantDeleteDto {
    private String sessionId;
    private Long userId;
}
