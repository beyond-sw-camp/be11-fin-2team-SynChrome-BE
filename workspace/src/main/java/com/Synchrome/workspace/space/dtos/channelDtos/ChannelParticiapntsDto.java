package com.Synchrome.workspace.space.dtos.channelDtos;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChannelParticiapntsDto {
    private String userId;
    private String name;
    private String email;
    private String profile;
}
