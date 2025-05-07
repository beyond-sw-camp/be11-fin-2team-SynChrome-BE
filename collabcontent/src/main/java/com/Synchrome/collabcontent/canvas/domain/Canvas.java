package com.Synchrome.collabcontent.canvas.domain;

import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
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
public class Canvas extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String ydocState;

    private Long userId;
    private Long channelId;
    private Long workspaceId;

    public void updateYdocState(String ydocState){
        this.ydocState = ydocState;
    }
}
