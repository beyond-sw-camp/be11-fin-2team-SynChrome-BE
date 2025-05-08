package com.Synchrome.collabcontent.chat.domain;


import com.Synchrome.collabcontent.chat.domain.ENUM.NotificationType;
import com.Synchrome.collabcontent.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long fromUserId;
    private Long roomId;
    private String message;
    private LocalDateTime timestamp;
    private Long chatMessageId;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(name = "is_read", nullable = false)
    private boolean read = false;
    private String workspaceTitle;
    private Long workspaceId;

    public void markAsRead() {
        this.read = true;
    }

}
