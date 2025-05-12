package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.ColorWorkspace;
import com.Synchrome.workspace.calendar.domain.Enum.ColorWorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColorWorkspaceRepository extends JpaRepository<ColorWorkspace, Long> {
    Optional<ColorWorkspace> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);
    @Query("SELECT cw FROM ColorWorkspace cw JOIN FETCH cw.workspace WHERE cw.userId = :userId")
    List<ColorWorkspace> findByUserIdWithWorkspace(@Param("userId") Long userId);
    List<ColorWorkspace> findByUserId(Long userId);
    List<ColorWorkspace> findByWorkspaceId(Long workspaceId);
    boolean existsByUserIdAndType(Long userId, ColorWorkspaceType type);
    long countByType(ColorWorkspaceType type);
}
