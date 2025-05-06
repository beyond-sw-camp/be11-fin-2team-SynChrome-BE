package com.Synchrome.collabcontent.livechat.repository;

import com.Synchrome.collabcontent.livechat.domain.IsEnded;
import com.Synchrome.collabcontent.livechat.domain.LiveChat;
import com.Synchrome.collabcontent.livechat.domain.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants,Long> {
    Optional<Participants> findByUserId(Long userId);
    Optional<Participants> findByUserIdAndLiveChat_SessionId(Long userId, String sessionId);
}
