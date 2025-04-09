package com.Synchrome.workspace.space.dtos.channelDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindMyChannelDto {
    private Long userId;
}
