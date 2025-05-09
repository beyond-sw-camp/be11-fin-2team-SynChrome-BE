package com.Synchrome.workspace.calendar.domain;

import com.Synchrome.workspace.calendar.domain.Enum.RepeatType;
import com.Synchrome.workspace.calendar.dto.EventDto;
import com.Synchrome.workspace.calendar.dto.UpdateEventDto;
import com.Synchrome.workspace.space.domain.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkSpace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    private ColorWorkspace colorWorkspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    private ColorCategory colorCategory;

//        반복일정 엔티티
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RepeatType repeatType = RepeatType.NONE; //(NONE, DAILY, WEEKLY, MONTHLY, YEARLY)
    private LocalDateTime repeatUntil; //반복종료일

    @OneToMany(mappedBy = "originEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventException> exceptions = new ArrayList<>();


    public void update(UpdateEventDto dto, ColorCategory category) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.colorCategory = category;
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.repeatType = dto.getRepeatType();
        this.repeatUntil = dto.getRepeatUntil();
    }
    public void setRepeatUntil(LocalDateTime repeatUntil) {
        this.repeatUntil = repeatUntil;
    }
}
