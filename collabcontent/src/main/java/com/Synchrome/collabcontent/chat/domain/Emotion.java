package com.Synchrome.collabcontent.chat.domain;

import com.Synchrome.collabcontent.chat.domain.ENUM.Del;
import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Emotion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Long messageId;
    private Long userId;
    private String emotion;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Del del = Del.N;
}
