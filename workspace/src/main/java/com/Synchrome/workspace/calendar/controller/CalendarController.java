package com.Synchrome.workspace.calendar.controller;

import com.Synchrome.workspace.calendar.dto.ColorCategoryDto;
import com.Synchrome.workspace.calendar.dto.ColorWorkspaceDto;
import com.Synchrome.workspace.calendar.dto.EventDto;
import com.Synchrome.workspace.calendar.dto.EventExceptionRequestDto;
import com.Synchrome.workspace.calendar.service.CalendarService;
import com.Synchrome.workspace.calendar.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final EventService eventService;

    public CalendarController(CalendarService calendarService, EventService eventService) {
        this.calendarService = calendarService;
        this.eventService = eventService;
    }
//      캘린더 생성
    @PostMapping("/create/user")
    public ResponseEntity<Long> createCalendar(@RequestHeader("X-User-Id") Long userId) {
        Long calendarId = calendarService.createCalendar(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarId);
    }

//    일정추가
    @PostMapping("/{calendarId}/event/create")
    public ResponseEntity<EventDto> createEvent(
            @PathVariable Long calendarId,
            @RequestBody EventDto request
    ) {
        EventDto created = eventService.createEvent(calendarId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
//        일정수정
    @PutMapping("/event/update/{eventId}")
    public ResponseEntity<EventDto> updateEvent( @PathVariable Long eventId,@RequestBody EventDto request){
        EventDto updated = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(updated);
    }
//   일정조회
    @GetMapping("/{calendarId}/event")
    public ResponseEntity<List<EventDto>> getEvent(
            @RequestParam(required = false) List<Long> workspaceIds,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<Long> colorCategoryIds,
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<EventDto> events = eventService.getEvent(start, end, workspaceIds, colorCategoryIds, userId);
        return ResponseEntity.ok(events);
    }
//    일정삭제
    @DeleteMapping("/event/delete/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

//    그룹id를 통한 반복일정 전체삭제
    @DeleteMapping("/group/delete/{groupId}")
    public ResponseEntity<Void> deleteEventsByGroupId(@PathVariable Long groupId) {
        try {
            eventService.deleteByGroupId(groupId);
            return ResponseEntity.noContent().build(); // 204
        } catch (Exception e) {
            // 예외 로깅 및 500 응답 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

// 반복일정 수정,삭제
    @PatchMapping("/event/{eventId}/exception")
    public ResponseEntity<Void> handleEventException(
            @PathVariable Long eventId,
            @RequestBody EventExceptionRequestDto request
            ){
        eventService.saveEventException(eventId, request);
        return ResponseEntity.ok().build();
    }

//    원본일정 삭제시 다음 반복일정 승격(다음반복일정이 원본일정이 됨)
@DeleteMapping("/promote-or-delete/{eventId}")
public ResponseEntity<?> promoteOrDelete(@PathVariable Long eventId) {
    if (!eventService.existsById(eventId)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("해당 ID의 일정이 존재하지 않습니다.");
    }
    try {
        EventDto promoted = eventService.promoteOrDelete(eventId);
        return ResponseEntity.ok(promoted); // 승격됨
    } catch (IllegalStateException e) {
        return ResponseEntity.noContent().build(); // 삭제됨
    }
}

    // 카테고리 생성
    @PostMapping("/color-category")
    public ResponseEntity<ColorCategoryDto> createColorCategory(
            @RequestBody ColorCategoryDto dto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createColorCategory(dto, userId));
    }

    // 카테고리 전체 조회
    @GetMapping("/color-category")
    public ResponseEntity<List<ColorCategoryDto>> getColorCategories(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(eventService.getColorCategories(userId));
    }

    // 카테고리 수정
    @PutMapping("/color-category/{id}")
    public ResponseEntity<ColorCategoryDto> updateColorCategory(
            @PathVariable Long id,
            @RequestBody ColorCategoryDto dto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(eventService.updateColorCategory(id, dto, userId));
    }

    // 카테고리 삭제
    @DeleteMapping("/color-category/{id}")
    public ResponseEntity<Void> deleteColorCategory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        eventService.deleteColorCategory(id, userId);
        return ResponseEntity.noContent().build();
    }

    //  유저별 워크스페이스라벨 색상 전체 조회
    @GetMapping("/color-workspaces")
    public ResponseEntity<List<ColorWorkspaceDto>> getColorWorkspaces(
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<ColorWorkspaceDto> result = eventService.getAllByUserId(userId);
        return ResponseEntity.ok(result);
    }

    // 색상 변경
    @PatchMapping("/color-workspace")
    public ResponseEntity<Void> updateColor(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ColorWorkspaceDto dto
    ) {
        eventService.updateColor(userId, dto.getWorkspaceId(), dto.getColor());
        return ResponseEntity.ok().build();
    }
}
