package com.Synchrome.workspace.space.dtos.channelDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelUpdateDto {
    private Long channelId;
    private String title;
    private Long sectionId;
}
