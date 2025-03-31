package com.Synchrome.collabcontent.livechat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class LiveChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable = false)
//    private Long channelId;
    @Column(nullable = false)
    private String sessionId;
//    @Column(nullable = false)
//    private Long participantsId;
    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsEnded isEnded = IsEnded.N;

    public void liveChatStop(){
        this.isEnded = IsEnded.Y;
    }
}
