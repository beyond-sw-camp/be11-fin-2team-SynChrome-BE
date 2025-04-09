package com.Synchrome.workspace.space.dtos.workSpaceDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkSpaceUpdateDto {
    private Long workSpaceId;
    private Long userId;
    private String title;
//    private MultipartFile logo;
}
