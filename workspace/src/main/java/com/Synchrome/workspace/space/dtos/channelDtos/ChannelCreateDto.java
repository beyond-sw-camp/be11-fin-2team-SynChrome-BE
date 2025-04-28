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
public class ChannelCreateDto {
    private String title;
    private Long userId;
    private Long sectionId;
    private Owner owner;
}
