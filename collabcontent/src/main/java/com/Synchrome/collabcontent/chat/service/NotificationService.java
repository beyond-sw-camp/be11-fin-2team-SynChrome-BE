package com.Synchrome.collabcontent.chat.service;

import com.Synchrome.collabcontent.chat.domain.ENUM.NotificationType;
import com.Synchrome.collabcontent.chat.domain.Notification;
import com.Synchrome.collabcontent.chat.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public Notification saveNotification(Long userId, Long fromUserId, Long roomId, String message, Long chatMessageId, NotificationType mention) {
        Notification notification = Notification.builder()
                .userId(userId)
                .fromUserId(fromUserId)
                .roomId(roomId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .chatMessageId(chatMessageId)
                .type(mention)
                .build();
        return notificationRepository.save(notification);
    }
    // ✅ 유저별 알림 리스트 조회 추가
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification noti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림 없음"));
        noti.markAsRead(); // 엔티티에 메서드 만들자
    }

    public Notification saveInviteNotification(Long userId, Long fromUserId, Long workspaceId, String message, NotificationType type) {
        return saveNotification(userId, fromUserId, workspaceId, message, null, type);
    }
}
