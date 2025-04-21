package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InviteAcceptDto {
    private Long userId;
    private Long workspaceId;
}
