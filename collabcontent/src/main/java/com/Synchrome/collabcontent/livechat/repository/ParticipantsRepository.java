package com.Synchrome.collabcontent.livechat.repository;

import com.Synchrome.collabcontent.livechat.domain.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants,Long> {
}
