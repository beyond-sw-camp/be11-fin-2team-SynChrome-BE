package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.Calendar;
import com.Synchrome.workspace.space.domain.ENUM.Del;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    boolean existsByUserId(Long userId);
    List<Calendar> findByUserIdAndDel(Long UserId, Del del);
    Optional<Calendar> findByUserId(Long userId);
}
