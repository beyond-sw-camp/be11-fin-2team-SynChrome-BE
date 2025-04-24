package com.Synchrome.collabcontent.chat.service;

import com.Synchrome.collabcontent.chat.domain.Notification;
import com.Synchrome.collabcontent.chat.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public Notification saveNotification(Long userId, Long fromUserId, Long roomId, String message, Long chatMessageId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .fromUserId(fromUserId)
                .roomId(roomId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .chatMessageId(chatMessageId)
                .build();
        return notificationRepository.save(notification);
    }
    // ✅ 유저별 알림 리스트 조회 추가
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
