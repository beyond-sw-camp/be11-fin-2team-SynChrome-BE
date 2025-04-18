package com.Synchrome.workspace.calendar.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id",
    foreignKey = @ForeignKey(name = "FK_event_id",
                             foreignKeyDefinition = "FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE SET NULL"))
    private Event originEvent; //반복일정들의 원본 일정

    private LocalDateTime exceptionDate; // 예외 처리할 날짜
    private boolean deleted;             // 삭제된 회차인지 확인
    private String newTitle;             // 수정된 내용
    private String newContent;
    private String newColor;
    private String newColorLabel;
    private LocalDateTime newStartTime;
    private LocalDateTime newEndTime;
    private Long groupId;
    @ManyToOne
    private ColorCategory newColorCategory;


}
