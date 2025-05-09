package com.Synchrome.workspace.calendar.service;

import com.Synchrome.workspace.calendar.domain.Calendar;
import com.Synchrome.workspace.calendar.domain.ColorWorkspace;
import com.Synchrome.workspace.calendar.domain.Enum.ColorWorkspaceType;
import com.Synchrome.workspace.calendar.repository.CalendarRepository;
import com.Synchrome.workspace.calendar.repository.ColorWorkspaceRepository;
import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.UserInfoDto;
import com.Synchrome.workspace.space.repository.WorkSpaceRepository;
import com.Synchrome.workspace.space.service.WorkSpaceFeign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
@Slf4j
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final ColorWorkspaceRepository colorWorkspaceRepository;
    private final RedisTemplate<String, Object> userInfoRedisTemplate;

    public CalendarService(CalendarRepository calendarRepository, WorkSpaceRepository workSpaceRepository, ColorWorkspaceRepository colorWorkspaceRepository, RedisTemplate<String, Object> userInfoRedisTemplate) {
        this.calendarRepository = calendarRepository;
        this.workSpaceRepository = workSpaceRepository;
        this.colorWorkspaceRepository = colorWorkspaceRepository;
        this.userInfoRedisTemplate = userInfoRedisTemplate;
    }
//    캘린더 생성
    public Long createCalendar(Long userId) {
        // 중복 방지
        boolean exists = calendarRepository.existsByUserId(userId);  // 수정
        if (calendarRepository.existsByUserId(userId)) {
            throw new IllegalStateException("이미 존재하는 유저 캘린더입니다.");
        }

        //  workSpace 없이 Calendar만 생성
        Calendar calendar = Calendar.builder()
                .name("기본 캘린더")  // 기본 이름
                .userId(userId)
                .build();

        Calendar savedCalendar = calendarRepository.save(calendar);
//
        String redisKey = String.valueOf(userId);
        String userInfoJson = (String) userInfoRedisTemplate.opsForValue().get(redisKey);

        String userName = "사용자";
        if (userInfoJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                UserInfoDto userInfo = objectMapper.readValue(userInfoJson, UserInfoDto.class);
                userName = userInfo.getName(); // Redis에 있을 경우만 반영
            } catch (JsonProcessingException e) {
                // 역직렬화 실패 시 기본값 유지
                log.warn("Redis 유저 정보 역직렬화 실패 (userId={}): {}", userId, e.getMessage());
            }
        } else {
            log.warn("Redis에 유저 정보가 없습니다 (userId={})", userId);
        }

        // 2. '개인' colorWorkspace 생성
        if (!colorWorkspaceRepository.existsByUserIdAndType(userId, ColorWorkspaceType.PRIVATE)) {
            ColorWorkspace personalLabel = ColorWorkspace.builder()
                    .userId(userId)
                    .workspace(null)
                    .name(userName)
                    .color("#9E9E9E")
                    .type(ColorWorkspaceType.PRIVATE)
                    .build();

            colorWorkspaceRepository.save(personalLabel);
        }

        return savedCalendar.getId();
        }

    public Long findOrCreateCalendarIdByUserId(Long userId) {
        Long calendar = calendarRepository.findByUserId(userId)
                .map(Calendar::getId)
                .orElseGet(() -> createCalendar(userId));
        return calendar;
    }
    }