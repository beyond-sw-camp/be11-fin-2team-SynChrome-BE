package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.domain.WorkSpaceParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkSpaceParticipantRepository extends JpaRepository<WorkSpaceParticipant,Long> {
    boolean existsByUserIdAndWorkSpace(Long userId, WorkSpace workSpace);
}
