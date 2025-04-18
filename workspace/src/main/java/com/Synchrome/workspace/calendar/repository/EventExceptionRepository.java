package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Synchrome.workspace.calendar.domain.EventException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventExceptionRepository extends JpaRepository<EventException, Long> {
    List<EventException> findByOriginEvent_Id(Long eventId);
    List<EventException> findByOriginEventIdAndExceptionDateBetween(Long eventId, LocalDateTime start, LocalDateTime end);
    List<EventException> findByOriginEvent_GroupId(Long groupId);
    void deleteByOriginEvent(Event evnent);
}
