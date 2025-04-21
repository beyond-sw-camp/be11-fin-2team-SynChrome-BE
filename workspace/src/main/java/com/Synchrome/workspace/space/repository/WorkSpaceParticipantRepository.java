package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.domain.WorkSpaceParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkSpaceParticipantRepository extends JpaRepository<WorkSpaceParticipant,Long> {
    boolean existsByUserIdAndWorkSpace(Long userId, WorkSpace workSpace);
    List<WorkSpaceParticipant> findByUserIdAndDel(Long userId, Del del);
    boolean existsByWorkSpaceIdAndUserIdAndDel(Long workSpaceId, Long userId, Del del);
    List<WorkSpaceParticipant> findByWorkSpaceIdAndDel(Long workspaceId, Del del);
}
