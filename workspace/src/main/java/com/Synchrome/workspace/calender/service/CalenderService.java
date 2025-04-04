package com.Synchrome.workspace.calender.service;

import com.Synchrome.workspace.calender.dto.EventDto;
import com.Synchrome.workspace.calender.repository.CalenderRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CalenderService {

    private final CalenderRepository calenderRepository;

    public CalenderService(CalenderRepository calenderRepository) {
        this.calenderRepository = calenderRepository;
    }

    public EventDto createEvent(EventDto eventDto) {

    }
}
