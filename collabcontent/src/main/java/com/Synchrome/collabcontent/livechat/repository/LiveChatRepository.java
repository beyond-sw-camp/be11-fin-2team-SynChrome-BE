package com.Synchrome.collabcontent.livechat.repository;


import com.Synchrome.collabcontent.livechat.domain.IsEnded;
import com.Synchrome.collabcontent.livechat.domain.LiveChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveChatRepository extends JpaRepository<LiveChat,Long> {
    Optional<LiveChat> findBySessionId(String sessionId);
    boolean existsBySessionId(String sessionId);
    Optional<LiveChat> findByChannelId(Long channelId);
    Optional<LiveChat> findTopByChannelIdAndIsEndedOrderByCreatedTimeDesc(Long channelId, IsEnded isEnded);
}
