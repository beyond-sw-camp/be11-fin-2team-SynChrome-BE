package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.ColorCategory;
import com.Synchrome.workspace.calendar.domain.Enum.RepeatType;
import com.Synchrome.workspace.calendar.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventDto {

    private Long id;
    private String customId;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
//      반복일정 엔티티
    private RepeatType repeatType;
    private LocalDateTime repeatUntil;
    private Long groupId;
    private Long colorCategoryId;
    private ColorCategoryDto colorCategory;
    private Long workspaceId;
    private Long colorWorkspaceId;
    private ColorWorkspaceDto colorWorkspace;

//        응답값
    public static EventDto from(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .colorCategoryId(event.getColorCategory() != null ? event.getColorCategory().getId() : null)
                .workspaceId(event.getWorkspace() != null ? event.getWorkspace().getId() : null)
                .colorWorkspaceId(event.getColorWorkspace() != null ? event.getColorWorkspace().getId() : null)
                .colorWorkspace(event.getColorWorkspace() != null ? ColorWorkspaceDto.from(event.getColorWorkspace()) : null)
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .repeatType(event.getRepeatType())
                .repeatUntil(event.getRepeatUntil())
                .groupId(event.getGroupId())
                .build();
    }
}
