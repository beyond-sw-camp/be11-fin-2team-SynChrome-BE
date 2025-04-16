package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.Channel;
import com.Synchrome.workspace.space.domain.ChannelParticipant;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelParticipantRepository extends JpaRepository<ChannelParticipant,Long> {
    List<ChannelParticipant> findByUserId(Long userId);
}
