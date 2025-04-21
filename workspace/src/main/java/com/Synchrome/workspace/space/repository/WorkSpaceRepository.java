package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace,Long> {
    Optional<List<WorkSpace>> findByUserIdAndDel(Long userId, Del del);
    Optional<WorkSpace> findByIdAndDel(Long id, Del del);
    Optional<WorkSpace> findByInviteUrlAndDel(String inviteUrl, Del del);
    boolean existsByInviteUrl(String inviteUrl);
}
