package com.Synchrome.collabcontent.chat.service;

import com.Synchrome.collabcontent.chat.domain.ENUM.NotificationType;
import com.Synchrome.collabcontent.chat.domain.Notification;
import com.Synchrome.collabcontent.chat.dto.NotificationDto;
import com.Synchrome.collabcontent.chat.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationRepository notificationRepository, SimpMessageSendingOperations messagingTemplate, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
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

    public void createAndPushNotification(NotificationDto dto) {
        // 타임스탬프가 안 들어왔을 경우 대비
        LocalDateTime timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now();

        // DB에 저장
        Notification entity = Notification.builder()
                .userId(dto.getUserId())
                .fromUserId(dto.getFromUserId())
                .roomId(dto.getRoomId())
                .message(dto.getMessage())
                .timestamp(timestamp)
                .chatMessageId(dto.getChatMessageId())
                .type(dto.getType())
                .workspaceTitle(dto.getWorkspaceTitle())
                .workspaceId(dto.getWorkspaceId())
                .read(false)
                .build();

        notificationRepository.save(entity);

        // 실시간 알림 전송
        try {
            dto.setTimestamp(timestamp);
            dto.setRead(false);
            String json = objectMapper.writeValueAsString(dto);
            messagingTemplate.convertAndSend("/topic/notify/" + dto.getUserId(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알림 WebSocket 전송 실패", e);
        }
    }
}
