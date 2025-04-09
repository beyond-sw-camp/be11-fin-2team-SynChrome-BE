package com.Synchrome.workspace.space.dtos.channelDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChannelCreateDto {
    private String title;
    private Long userId;
    private Long sectionId;
}
