package com.Synchrome.workspace.space.dtos.sectionDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FindMySectionDto {
    private Long userId;
    private Long workSpaceId;
}
