package com.Synchrome.workspace.calendar.service;

import com.Synchrome.workspace.calendar.domain.Calendar;
import com.Synchrome.workspace.calendar.repository.CalendarRepository;
import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.repository.WorkSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final WorkSpaceRepository workSpaceRepository;

    public CalendarService(CalendarRepository calendarRepository, WorkSpaceRepository workSpaceRepository) {
        this.calendarRepository = calendarRepository;
        this.workSpaceRepository = workSpaceRepository;
    }
//    캘린더 생성
    public Long createCalendar(Long userId) {
        // 중복 방지
        // 중복 방지
        boolean exists = calendarRepository.existsByUserId(userId);  // ✅ 수정
        if (calendarRepository.existsByUserId(userId)) {
            throw new IllegalStateException("이미 존재하는 유저 캘린더입니다.");
        }

        // ✅ workSpace 없이 Calendar만 생성
        Calendar calendar = Calendar.builder()
                .name("기본 캘린더")  // 기본 이름
                .userId(userId)
                .build();

        Calendar savedCalendar = calendarRepository.save(calendar);
        return savedCalendar.getId();
        }
    }