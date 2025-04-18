package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CalendarDto {
    private Long id;
    private String name;
    private Long userId;
    private Long workspaceId;
    public static CalendarDto from(Calendar calendar) {
        return CalendarDto.builder()
                .id(calendar.getId())
                .name(calendar.getName())
                .userId(calendar.getUserId())
                .workspaceId(calendar.getWorkSpace().getId())
                .build();
    }
}
