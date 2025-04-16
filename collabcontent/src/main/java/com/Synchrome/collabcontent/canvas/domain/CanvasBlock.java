package com.Synchrome.collabcontent.canvas.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanvasBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long canvasId;

    private String orderKey;

    private int indent;

    private String type;

    @Lob
    private String content; // Tiptap JSON (문자열 형태로 저장)
}
