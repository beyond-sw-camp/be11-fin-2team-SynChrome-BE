package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.Enum.RepeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateEventDto {

    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RepeatType repeatType;
    private LocalDateTime repeatUntil;
    private Long colorCategoryId;
//    private ColorCategoryDto colorCategory;
    private Long workspaceId;
}
