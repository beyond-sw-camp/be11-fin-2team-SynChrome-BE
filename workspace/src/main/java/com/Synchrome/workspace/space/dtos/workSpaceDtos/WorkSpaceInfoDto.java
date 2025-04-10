package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import com.Synchrome.workspace.space.dtos.channelDtos.ChannelResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkSpaceInfoDto {
    private Long sectionId;
    private String title;
    private List<ChannelResDto> channels;
}
