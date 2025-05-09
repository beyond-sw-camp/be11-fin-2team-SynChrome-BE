package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.ColorWorkspace;
import com.Synchrome.workspace.calendar.domain.Enum.ColorWorkspaceType;
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
    private ColorWorkspaceType type;

    public static ColorWorkspaceDto from(ColorWorkspace cw) {
        if (cw == null) return null;

        return ColorWorkspaceDto.builder()
                .workspaceId(cw.getWorkspace() != null ? cw.getWorkspace().getId() : null)
                .workspaceName(cw.getWorkspace() != null ? cw.getWorkspace().getTitle() : null)
                .color(cw.getColor())
                .type(cw.getType())
                .build();
    }
}
