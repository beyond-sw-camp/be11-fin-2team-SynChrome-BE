package com.Synchrome.collabcontent.LiveChat.Repository;

import com.Synchrome.collabcontent.LiveChat.Domain.LiveChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LiveChatRepository extends JpaRepository<LiveChat,Long> {
    Optional<LiveChat> findBySessionId(String sessionId);
    boolean existsBySessionId(String sessionId);
}
