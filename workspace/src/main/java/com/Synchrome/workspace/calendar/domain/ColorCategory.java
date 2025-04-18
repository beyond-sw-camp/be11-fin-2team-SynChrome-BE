package com.Synchrome.workspace.calendar.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
public class ColorCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    private Long userId;

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
