package com.Synchrome.workspace.space.dtos.sectionDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opensearch.common.inject.ImplementedBy;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MySectionResDto {
    private Long sectionId;
    private String title;
}
