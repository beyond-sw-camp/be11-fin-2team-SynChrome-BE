package com.Synchrome.workspace.calendar.service;

import com.Synchrome.workspace.calendar.domain.*;
import com.Synchrome.workspace.calendar.domain.Enum.RepeatType;
import com.Synchrome.workspace.calendar.dto.ColorCategoryDto;
import com.Synchrome.workspace.calendar.dto.ColorWorkspaceDto;
import com.Synchrome.workspace.calendar.dto.EventDto;
import com.Synchrome.workspace.calendar.dto.EventExceptionRequestDto;
import com.Synchrome.workspace.calendar.repository.*;
import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.repository.WorkSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final EventExceptionRepository eventExceptionRepository;
    private final ColorCategoryRepository colorCategoryRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final ColorWorkspaceRepository colorWorkspaceRepository;

    public EventService(EventRepository eventRepository, CalendarRepository calendarRepository, EventExceptionRepository eventExceptionRepository, ColorCategoryRepository colorCategoryRepository, WorkSpaceRepository workSpaceRepository, ColorWorkspaceRepository colorWorkspaceRepository) {
        this.eventRepository = eventRepository;
        this.calendarRepository = calendarRepository;
        this.eventExceptionRepository = eventExceptionRepository;
        this.colorCategoryRepository = colorCategoryRepository;
        this.workSpaceRepository = workSpaceRepository;
        this.colorWorkspaceRepository = colorWorkspaceRepository;
    }

    //        일정추가
    public EventDto createEvent(Long calendarId, EventDto dto) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new EntityNotFoundException("캘린더를 찾을 수 없습니다."));
        // 반복일정 기본처리
        RepeatType repeatType = dto.getRepeatType() != null ? dto.getRepeatType() : RepeatType.NONE;
        LocalDateTime repeatUntil = dto.getRepeatUntil();
        Long groupId = dto.getGroupId() != null ? dto.getGroupId() : System.currentTimeMillis();
        WorkSpace workspace = workSpaceRepository.findById(dto.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("워크스페이스를 찾을 수 없습니다."));

        ColorCategory colorCategory = null;

        if (dto.getColorCategoryId() != null) {
            colorCategory = colorCategoryRepository.findById(dto.getColorCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
        }
        // 응답값 정의
        Event event = Event.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .colorCategory(colorCategory)
                .workspace(workspace)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .repeatType(repeatType)
                .repeatUntil(repeatUntil)
                .calendar(calendar)
                .groupId(groupId)
                .build();

        Event saved = eventRepository.save(event);
        return EventDto.from(saved);
    }

    //        일정수정
    public EventDto updateEvent(Long eventId, EventDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        ColorCategory colorCategory = null;
        if (dto.getColorCategory() != null && dto.getColorCategory().getId() != null) {
            colorCategory = colorCategoryRepository.findById(dto.getColorCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
        }


        event.update(dto, colorCategory); // 엔티티 내부에 수정 메서드 존재 시
        return EventDto.from(event);
    }

    //        일정조회
    public List<EventDto> getEvent(LocalDateTime start, LocalDateTime end,
                                   List<Long> workspaceIds,
                                   List<Long> colorCategoryIds,
                                   Long userId) {
        List<Event> event = eventRepository.findAllByUserId(userId);
        List<EventDto> result = new ArrayList<>();
//        프론트쪽 호출예시 GET /calendar/5/event?start=2025-04-01T00:00:00&end=2025-04-30T23:59:59&colorCategoryIds=1,2,3

        for (Event e : event) {
            if (colorCategoryIds != null && !colorCategoryIds.isEmpty()) {
                if (e.getColorCategory() == null || !colorCategoryIds.contains(e.getColorCategory().getId())) {
                    continue;
                }
            }
            if (e.getRepeatType() == RepeatType.NONE) {
                if (!e.getStartTime().isAfter(end) && !e.getEndTime().isBefore(start)) {
                    result.add(EventDto.from(e));
                }
            } else {
                if (e.getRepeatUntil() == null || !e.getRepeatUntil().isBefore(start)) {
                    result.addAll(generateRepeats(e, start, end));
                }
            }
        }

        return result;
    }

    public List<EventDto> getFilteredEvents(LocalDateTime start, LocalDateTime end,
                                            List<Long> workspaceIds, List<Long> colorCategoryIds, Long userId) {
        List<Event> events = eventRepository.findAllByUserAndDate(userId, start, end);

        return events.stream()
                .filter(e -> workspaceIds == null || workspaceIds.contains(e.getWorkspace().getId()))
                .filter(e -> colorCategoryIds == null ||
                        (e.getColorCategory() != null && colorCategoryIds.contains(e.getColorCategory().getId())))
                .map(EventDto::from)
                .toList();
    }


    //            조회 시 반복일정 가상처리(가상일정은 db저장x)
    private List<EventDto> generateRepeats(Event event, LocalDateTime start, LocalDateTime end) {
        List<EventDto> repeated = new ArrayList<>();
        LocalDateTime originalStart = event.getStartTime();
        LocalDateTime originalEnd = event.getEndTime();
        LocalDateTime until = event.getRepeatUntil() != null ? event.getRepeatUntil() : end;
        List<EventException> exception = eventExceptionRepository.findByOriginEvent_GroupId(event.getGroupId());
        List<Event> singleEvents = eventRepository.findByGroupIdAndRepeatType(event.getGroupId(), RepeatType.NONE);
        Duration duration = Duration.between(originalStart, originalEnd);
        LocalDateTime current = originalStart; //반복문 변수로 사용되기때문에 람다안에서 final 변수라고 판단X


        while (!current.isAfter(until)) {
            if (current.isAfter(end)) break;
            LocalDate currentDate = current.toLocalDate(); // 람다안에서 쓰기위해 final한 변수 생성


//          수정되서 가상에서 단독이 된 일정자리에 중복해서 가상일정생성 방지
            boolean isOverridden = singleEvents.stream().anyMatch(ev ->
                    ev.getStartTime().toLocalDate().equals(currentDate));
            if (isOverridden) {
                current = nextRepeat(current, event.getRepeatType());
                continue; // 이미 단독 이벤트가 있음 -> skip
            }

            boolean isDeleted = exception.stream().anyMatch(ex ->
                    ex.getExceptionDate().toLocalDate().equals(currentDate) && ex.isDeleted());
            if (isDeleted) {
                current = nextRepeat(current, event.getRepeatType());
                continue;
            }

//        Optional<EventException> overridden = exception.stream()
//                .filter(ex -> ex.getExceptionDate().toLocalDate().equals(currentDate) && !ex.isDeleted())
//                .findFirst();

            Optional<EventException> overridden = exception.stream()
                    .filter(ex -> ex.getExceptionDate().toLocalDate().equals(currentDate) && !ex.isDeleted())
                    .findFirst();

            Long id = currentDate.equals(originalStart.toLocalDate()) ? event.getId() : null;

//        사용자가 요청한(end)시간보다 늦지않고, 사용자가 요청한(start)시간보다 이르지않음.즉, 요청한 날짜안에있는 일정만 구분
            if (!current.isAfter(end) && !current.plus(duration).isBefore(start)) {
                String customId = event.getGroupId() + "_" + currentDate.format(DateTimeFormatter.BASIC_ISO_DATE);
//                수정한 예외일정이 있는경우
                if (overridden.isPresent()) {
                    EventException ex = overridden.get();
                    repeated.add(EventDto.builder()
                            .id(id)
                            .customId(customId)
                            .title(ex.getNewTitle() != null ? ex.getNewTitle() : event.getTitle())
                            .content(ex.getNewContent() != null ? ex.getNewContent() : event.getContent())
                            .colorCategory(ColorCategoryDto.from(ex.getNewColorCategory() != null ? ex.getNewColorCategory() : event.getColorCategory()))
                            .startTime(ex.getNewStartTime() != null ? ex.getNewStartTime() : current)
                            .endTime(ex.getNewEndTime() != null ? ex.getNewEndTime() : current.plus(duration))
                            .repeatType(event.getRepeatType())
                            .repeatUntil(event.getRepeatUntil())
                            .groupId(event.getGroupId())
                            .build());
                }
//                예외가 없는경우(일반적인 경우)
                else {
                    repeated.add(EventDto.builder()
                            .id(id)
                            .customId(customId)
                            .title(event.getTitle())
                            .content(event.getContent())
                            .colorCategory(ColorCategoryDto.from(event.getColorCategory()))
                            .startTime(current)
                            .endTime(current.plus(duration))
                            .repeatType(event.getRepeatType())
                            .repeatUntil(event.getRepeatUntil())
                            .groupId(event.getGroupId())
                            .build());
                }
            }
            current = nextRepeat(current, event.getRepeatType());
        }
        return repeated;
    }
//        주기별 다음 반복일정 확인 메서드
    private LocalDateTime nextRepeat(LocalDateTime current, RepeatType repeatType) {
        return switch (repeatType) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
            default -> current;
        };
    }
//        예외일정 저장
    public void saveEventException(Long eventId, EventExceptionRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("원본 일정을 찾을 수 없습니다."));

        Duration duration = Duration.between(event.getStartTime(), event.getEndTime());
        ColorCategory colorCategory = null;
        if (dto.getNewColorCategoryId() != null) {
            colorCategory = colorCategoryRepository.findById(dto.getNewColorCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

            EventException exception = EventException.builder()
                    .originEvent(event)
                    .exceptionDate(dto.getExceptionDate())
                    .deleted(dto.isDeleted())
                    .newTitle(dto.getNewTitle())
                    .newContent(dto.getNewContent())
                    .newColorCategory(colorCategory)
                    .newStartTime(dto.getNewStartTime())
                    .newEndTime(dto.getNewEndTime())
                    .build();


//        수정이면 새로운 일정으로 event에 저장
            if (!dto.isDeleted()) {
                Event newEvent = Event.builder()
                        .title(dto.getNewTitle() != null ? dto.getNewTitle() : event.getTitle())
                        .content(dto.getNewContent() != null ? dto.getNewContent() : event.getContent())
                        .startTime(dto.getNewStartTime() != null ? dto.getNewStartTime() : dto.getExceptionDate())
                        .endTime(dto.getNewEndTime() != null ? dto.getNewEndTime() : dto.getExceptionDate().plus(duration))
                        .colorCategory(colorCategory)
                        .repeatType(RepeatType.NONE) // 단독 일정으로
                        .groupId(event.getGroupId()) // 반복 그룹 유지
                        .calendar(event.getCalendar())
                        .workspace(event.getWorkspace())
                        .build();
                eventRepository.save(newEvent);
            }
            eventExceptionRepository.save(exception);

        }
    }

    //        일정삭제
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        event.getExceptions().clear();
        eventRepository.delete(event); // 하드딜리트 or 소프트딜리트 선택 가능
    }

    //    반복일정 전체 삭제
    public void deleteByGroupId(Long groupId) {
        List<Event> events = eventRepository.findByGroupId(groupId);

        if (events.isEmpty()) {
            log.warn("삭제할 이벤트가 없습니다. groupId: {}", groupId);
            return;
        }

        eventRepository.deleteAll(events);
        log.info("총 {}개의 이벤트가 삭제되었습니다. groupId: {}", events.size(), groupId);
    }

//        승격과 삭제
    public EventDto promoteOrDelete(Long eventId) {
        try {
            EventDto promoted = promoteNextAvailableRepeat(eventId);
            return promoted;
        } catch (IllegalStateException e) {
            e.printStackTrace(); // 전체 스택 출력 // 예외 발생 시에도 삭제
            return null;
        }
        finally {
            if(existsById(eventId)) {
                deleteEvent(eventId);
            }
        }
    }
//        다음 가상일정 승격
    public EventDto promoteNextAvailableRepeat(Long eventId) {

        Event original = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("원본 일정을 찾을 수 없습니다."));

        List<EventException> exceptions = eventExceptionRepository.findByOriginEvent_GroupId(original.getGroupId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime current = nextRepeat(original.getStartTime(), original.getRepeatType());
        LocalDateTime until = original.getRepeatUntil();
//        무한루프 카운터
        int counter = 0;
        int limit = 1000;
        // 반복문으로 삭제/수정되지 않은 일정 찾기
        while (!current.isAfter(until)) {
            if (counter++ > limit) {
                throw new IllegalStateException("무한 루프 가능성 감지됨.");
            }
            if (current.isBefore(now)) {
                current = nextRepeat(current, original.getRepeatType());
                continue;
            }

            LocalDate currentDate = current.toLocalDate();
//        삭제여부 확인 변수 설정
            boolean isDeleted = exceptions.stream().anyMatch(ex ->
                    ex.getExceptionDate().toLocalDate().equals(currentDate) && ex.isDeleted());
//
            boolean isOverridden = exceptions.stream().anyMatch(ex ->
                    ex.getExceptionDate().toLocalDate().equals(currentDate) && !ex.isDeleted() &&
                            (ex.getNewTitle() != null || ex.getNewStartTime() != null ||
                                    ex.getNewEndTime() != null || ex.getNewContent() != null ||
                                    ex.getNewColor() != null || ex.getNewColorLabel() != null));

            if (!isDeleted && !isOverridden) {
                break;
            }
            current = nextRepeat(current, original.getRepeatType());
        }


        if (current.isAfter(until)) {
            throw new IllegalStateException("승격 가능한 회차가 존재하지 않습니다.");
        }

        //  기존 원본 Event의 반복 종료 시점 조정
        original.setRepeatUntil(current.minusDays(1));

        //  새로운 Event 생성 (승격된 일정 -> 새 원본)
        Duration duration = Duration.between(original.getStartTime(), original.getEndTime());
        Event newEvent = Event.builder()
                .title(original.getTitle())
                .content(original.getContent())
                .startTime(current)
                .endTime(current.plus(duration))
                .repeatType(original.getRepeatType())
                .repeatUntil(until)
                .calendar(original.getCalendar())
                .workspace(original.getWorkspace())
                .colorCategory(original.getColorCategory())
                .groupId(original.getGroupId())
                .build();

        Event saved = eventRepository.save(newEvent);

        LocalDate currentDate = current.toLocalDate();

        //  예외 중복 방지 -> 같은 날짜의 예외가 없을 때만 저장
        boolean alreadyExists = exceptions.stream().anyMatch(ex ->
                ex.getExceptionDate().toLocalDate().equals(currentDate));

        if (!alreadyExists) {
            EventException ex = EventException.builder()
                    .originEvent(original)
                    .exceptionDate(current)
                    .deleted(false)
                    .build();

            eventExceptionRepository.save(ex);
        }
        return EventDto.from(saved);
    }

    public boolean existsById(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    // 카테고리 생성
    public ColorCategoryDto createColorCategory(ColorCategoryDto dto, Long userId) {
        ColorCategory category = ColorCategory.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .userId(userId)
                .build();
        return ColorCategoryDto.from(colorCategoryRepository.save(category));
    }

    // 사용자별 카테고리 조회
    public List<ColorCategoryDto> getColorCategories(Long userId) {
        return colorCategoryRepository.findByUserId(userId).stream()
                .map(ColorCategoryDto::from)
                .toList();
    }

    // 카테고리 수정
    public ColorCategoryDto updateColorCategory(Long id, ColorCategoryDto dto, Long userId) {
        ColorCategory category = colorCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        if (!category.getUserId().equals(userId)) {
            throw new SecurityException("권한이 없습니다.");
        }

        category.update(dto.getName(), dto.getColor());
        return ColorCategoryDto.from(category);
    }

    // 카테고리 삭제
    public void deleteColorCategory(Long id, Long userId) {
        ColorCategory category = colorCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        if (!category.getUserId().equals(userId)) {
            throw new SecurityException("권한이 없습니다.");
        }

        colorCategoryRepository.delete(category);
    }

//    //워크스페이스라벨 자동생성 메서드
//    public void initColorWorkspaces(Long workspaceId, List<Long> memberUserIds) {
//        WorkSpace workspace = workSpaceRepository.getReferenceById(workspaceId);
//
//        for (Long userId : memberUserIds) {
//            boolean exists = colorWorkspaceRepository
//                    .findByUserIdAndWorkspaceId(userId, workspaceId)
//                    .isPresent();
//
//            if (!exists) {
//                String color = generateDefaultColor(userId, workspaceId);
//                ColorWorkspace cw = ColorWorkspace.create(userId, workspace, color);
//                colorWorkspaceRepository.save(cw);
//            }
//        }
//    }

    public List<ColorWorkspaceDto> getAllByUserId(Long userId) {
        return colorWorkspaceRepository.findByUserIdWithWorkspace(userId)
                .stream()
                .map(cw -> {
                    ColorWorkspaceDto dto = new ColorWorkspaceDto();
                    dto.setWorkspaceId(cw.getWorkspace().getId());
                    dto.setWorkspaceName(cw.getWorkspace().getTitle());
                    dto.setColor(cw.getColor());
                    return dto;
                }).collect(Collectors.toList());
    }

    public void updateColor(Long userId, Long workspaceId, String color) {
        ColorWorkspace cw = colorWorkspaceRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("컬러 설정이 존재하지 않습니다."));
        cw.changeColor(color);
    }

}
