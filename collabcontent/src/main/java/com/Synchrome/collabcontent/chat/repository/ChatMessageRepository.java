package com.Synchrome.collabcontent.chat.repository;

import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdAndParentIdIsNullOrderByIdDesc(Long roomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndIdLessThanAndParentIdIsNullOrderByIdDesc(Long roomId, Long beforeId, Pageable pageable);

    List<ChatMessage> findByParentIdOrderByIdDesc(Long parentId, Pageable pageable);

    List<ChatMessage> findByParentIdAndIdLessThanOrderByIdDesc(Long parentId, Long beforeId, Pageable pageable);
}