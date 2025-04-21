package com.Synchrome.workspace.space.dtos.channelDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChannelInviteDto {
    private Long channelId;
    private List<Long> userIds;
}
