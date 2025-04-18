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
    public void createCalendar(Long workspaceId, Long userId) {


        // 중복 방지
        boolean exists = calendarRepository.existsByWorkSpaceIdAndUserId(workspaceId, userId);
        if (exists) {
            throw new IllegalStateException("이미 존재하는 유저 캘린더입니다.");
        }else if(!exists) {
            WorkSpace workSpace = workSpaceRepository.findById(workspaceId)
                    .orElseThrow(() -> new EntityNotFoundException("워크스페이스를 찾을 수 없습니다."));
            Calendar calendar = Calendar.builder()
                    .name("캘린더")
                    .workSpace(workSpace)
                    .userId(userId)
                    .build();

            calendarRepository.save(calendar);
        }
    }
}
