package com.Synchrome.workspace.calendar.domain;

import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Getter
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "del",nullable = false)
    private Del del = Del.N;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Event> event = new ArrayList<>();

    public void delete(){
        this.del = Del.Y;
    }
}
