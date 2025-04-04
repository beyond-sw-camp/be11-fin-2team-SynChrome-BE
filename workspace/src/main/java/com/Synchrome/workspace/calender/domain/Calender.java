package com.Synchrome.workspace.calender.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Getter
public class Calender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long chanelId;

}
