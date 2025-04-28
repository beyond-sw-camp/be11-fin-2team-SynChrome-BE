package com.Synchrome.workspace.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ColorWorkspaceDto {
    private Long workspaceId;
    private String workspaceName;
    private String color;
}
