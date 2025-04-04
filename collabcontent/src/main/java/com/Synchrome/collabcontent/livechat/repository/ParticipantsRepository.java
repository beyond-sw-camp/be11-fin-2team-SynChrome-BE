package com.Synchrome.collabcontent.liveChat.Repository;

import com.Synchrome.collabcontent.liveChat.Domain.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants,Long> {
}
