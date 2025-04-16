package com.Synchrome.collabcontent.chat.repository;

import com.Synchrome.collabcontent.chat.domain.ChatRoom;
import com.Synchrome.collabcontent.chat.domain.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
    Optional<ReadStatus> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}