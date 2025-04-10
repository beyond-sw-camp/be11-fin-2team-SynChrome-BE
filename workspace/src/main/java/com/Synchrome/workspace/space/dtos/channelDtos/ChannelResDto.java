package com.Synchrome.workspace.space.dtos.channelDtos;

import com.Synchrome.workspace.space.domain.ENUM.Owner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChannelResDto {
    private Long channelId;
    private Long sectionId;
    private String title;
    private Owner owner;
}
