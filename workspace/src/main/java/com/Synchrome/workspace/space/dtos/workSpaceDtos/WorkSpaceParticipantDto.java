package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkSpaceParticipantDto {
    private String userId;
    private String name;
    private String email;
    private String profile;
}
