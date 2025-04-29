package com.Synchrome.workspace.space.dtos.sectionDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SectionDeleteDto {
    private Long userId;
    private Long sectionId;
}
