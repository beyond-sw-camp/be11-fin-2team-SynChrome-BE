package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.ColorCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventExceptionRequestDto {
    private LocalDateTime exceptionDate;
    private boolean deleted;
    private String newTitle;
    private String newContent;
    private Long newColorCategoryId;
    private LocalDateTime newStartTime;
    private LocalDateTime newEndTime;
    private Long groupId;
}
