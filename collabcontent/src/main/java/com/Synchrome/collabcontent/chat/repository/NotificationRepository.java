package com.Synchrome.collabcontent.chat.repository;

import com.Synchrome.collabcontent.chat.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
}
