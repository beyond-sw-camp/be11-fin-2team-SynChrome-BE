package com.Synchrome.collabcontent.livechat.domain;

import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class LiveChat extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String sessionId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsEnded isEnded = IsEnded.N;
    @OneToMany(mappedBy = "liveChat",cascade = CascadeType.ALL)
    @Builder.Default
    private List<Participants> participants = new ArrayList<>();



    public void liveChatStop(){
        this.isEnded = IsEnded.Y;
    }

}
