package com.Synchrome.collabcontent.canvas.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Canvas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String ydocState;


    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일자

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일자

    public void updateYdocState(String ydocState){
        this.ydocState = ydocState;
    }
}
