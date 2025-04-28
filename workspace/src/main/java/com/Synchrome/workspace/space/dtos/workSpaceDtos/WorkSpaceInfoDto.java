package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import com.Synchrome.workspace.space.domain.ENUM.Owner;
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
    private Long workspaceId;
    private String workspaceTitle;
    private Long sectionId;
    private Owner sectionOwner;
    private String title;
    private List<ChannelResDto> channels;
}
