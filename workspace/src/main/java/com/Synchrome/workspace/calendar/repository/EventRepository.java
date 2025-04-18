package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.Enum.RepeatType;
import com.Synchrome.workspace.calendar.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.calendar.id = :calendarId AND e.startTime >= :start AND e.endTime <= :end")
    List<Event> findAllByCalendarIdAndTimeRange(Long calendarId, LocalDateTime start, LocalDateTime end);
    List<Event> findByCalendarId(Long calendarId);
    List<Event> findByGroupIdAndRepeatType(Long groupId, RepeatType repeatType);
    List<Event> findByGroupId(Long groupId);
    @Query("SELECT e FROM Event e WHERE e.calendar.userId = :userId")
    List<Event> findAllByUserId(@Param("userId") Long userId);
    @Query("""
        SELECT e FROM Event e
        WHERE e.workspace.userId = :userId
        AND (
            (e.repeatType = 'NONE' AND e.startTime <= :end AND e.endTime >= :start)
            OR (e.repeatType <> 'NONE' AND (e.repeatUntil IS NULL OR e.repeatUntil >= :start))
        )
    """)
    List<Event> findAllByUserAndDate(@Param("userId") Long userId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}
