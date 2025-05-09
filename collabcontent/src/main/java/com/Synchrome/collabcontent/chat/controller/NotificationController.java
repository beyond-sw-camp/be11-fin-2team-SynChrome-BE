package com.Synchrome.collabcontent.chat.controller;

import com.Synchrome.collabcontent.chat.domain.Notification;
import com.Synchrome.collabcontent.chat.dto.NotificationDto;
import com.Synchrome.collabcontent.chat.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {



    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    @PutMapping("/read/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
    @PostMapping("/invite")
    public ResponseEntity<Void> sendInviteNotification(@RequestBody NotificationDto dto) {
        notificationService.createAndPushNotification(dto);
        return ResponseEntity.ok().build();
    }

}