package com.Synchrome.workspace.calendar.domain;

import com.Synchrome.workspace.space.domain.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Builder
@ToString
@Getter
public class ColorWorkspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workspace;

    @Column(nullable = false)
    private String color;

    protected ColorWorkspace() {} // JPA

    private ColorWorkspace(Long userId, WorkSpace workspace, String color) {
        this.userId = userId;
        this.workspace = workspace;
        this.color = color;
    }

    public static ColorWorkspace create(Long userId, WorkSpace workspace,  String color) {
        return new ColorWorkspace(userId, workspace, color);
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
