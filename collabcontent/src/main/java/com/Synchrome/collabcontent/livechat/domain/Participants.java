package com.Synchrome.collabcontent.livechat.domain;

import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Participants extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livechat_id")
    private LiveChat liveChat;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsEnded isEnded = IsEnded.N;


    public void deleteParticipant(){
        this.isEnded = IsEnded.Y;
    }
}
