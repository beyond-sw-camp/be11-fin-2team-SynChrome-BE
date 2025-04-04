package com.Synchrome.collabcontent.liveChat.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.Buffer;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ParticipantAddDto {
    private String sessionId;
    private Long userId;
}
