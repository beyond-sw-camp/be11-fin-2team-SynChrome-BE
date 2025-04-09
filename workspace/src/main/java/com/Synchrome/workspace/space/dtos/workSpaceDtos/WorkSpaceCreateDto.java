package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkSpaceCreateDto {
    private String title;
    private Long userId;
    private String inviteUrl;
}
