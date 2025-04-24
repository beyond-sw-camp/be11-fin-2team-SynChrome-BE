package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateGroupRoomReqDto {
    private Long userId;
    private String roomName;
}
