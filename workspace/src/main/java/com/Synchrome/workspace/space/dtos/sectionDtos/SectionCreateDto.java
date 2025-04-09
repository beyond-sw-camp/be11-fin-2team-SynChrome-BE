package com.Synchrome.workspace.space.dtos.sectionDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SectionCreateDto {
    private String title;
    private Long userId;
    private Long workSpaceId;
}
