package com.Synchrome.workspace.space.repository;

import com.Synchrome.workspace.space.domain.ENUM.Del;
import com.Synchrome.workspace.space.domain.Section;
import com.Synchrome.workspace.space.domain.WorkSpace;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section,Long> {
    Optional<Section> findByIdAndUserId(Long sectionId, Long userId);
    List<Section> findByUserIdAndWorkSpaceIdAndDel(Long userId, Long workSpaceId, Del del);
    List<Section> findByWorkSpaceIdAndDel(Long workSpaceId, Del del);

    @EntityGraph(attributePaths = "channels")
    List<Section> findByWorkSpaceIdAndUserIdAndDel(Long workSpaceId, Long userId, Del del);
}
