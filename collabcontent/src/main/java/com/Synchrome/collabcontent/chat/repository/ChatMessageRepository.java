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
    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.chatRoom = :chatRoom
      AND (:beforeTime IS NULL OR m.createdTime < :beforeTime)
      AND m.parent IS NULL
    ORDER BY m.createdTime DESC
""")
    List<ChatMessage> findPagedRootMessages(@Param("chatRoom") ChatRoom chatRoom,
                                            @Param("beforeTime") LocalDateTime beforeTime,
                                            Pageable pageable);

    List<ChatMessage> findByParentOrderByCreatedTimeAsc(ChatMessage parent);

}