package com.Synchrome.collabcontent.liveChat.Domain;

import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import com.Synchrome.collabcontent.liveChat.Repository.ParticipantsRepository;
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
}
