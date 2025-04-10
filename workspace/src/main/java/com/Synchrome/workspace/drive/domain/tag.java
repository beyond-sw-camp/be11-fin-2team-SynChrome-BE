package com.Synchrome.workspace.drive.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Getter
@Setter
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public tag(String name) {
        this.name = name;
    }
}
