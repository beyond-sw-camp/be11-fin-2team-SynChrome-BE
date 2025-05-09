package com.Synchrome.workspace.calendar.domain;

import com.Synchrome.workspace.calendar.domain.Enum.ColorWorkspaceType;
import com.Synchrome.workspace.space.domain.ENUM.Del;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "del", nullable = false)
    @Builder.Default
    private Del del = Del.N;  // 기본값 'N' (삭제 안 됨)

    @Column(nullable = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ColorWorkspaceType type;

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

    public void delete() {
        this.del = Del.Y;
    }
}
