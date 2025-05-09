package com.Synchrome.collabcontent.chat.domain;

import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import com.Synchrome.collabcontent.common.domain.DelYN;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private Long userId;

    @Column(nullable = false, length = 500)
    private String content;

    private Long parentId;

    private boolean presentUnreadMessage;

    private Long workspaceId;

    private Long replyTo;
    private String replyPreview;
    private String workspaceTitle;

    @Builder.Default
    private Long totalThreadCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DelYN delYN = DelYN.N;

    public void modifyContent(String content){
        this.content = content;
    }

    public void deleteMessage(DelYN delYN){
        this.delYN = delYN;
    }

    public void addThreadCount(){
        this.totalThreadCount++;
    }

    public void decreaseThreadCount(){
        this.totalThreadCount--;
    }
}