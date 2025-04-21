package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    boolean existsByWorkSpaceIdAndUserId(Long workspaceId, Long userId);
}
