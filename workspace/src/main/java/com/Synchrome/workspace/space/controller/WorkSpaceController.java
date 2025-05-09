package com.Synchrome.workspace.space.controller;

import com.Synchrome.workspace.space.domain.WorkSpace;
import com.Synchrome.workspace.space.dtos.channelDtos.*;
import com.Synchrome.workspace.space.dtos.sectionDtos.*;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.*;
import com.Synchrome.workspace.space.service.WorkSpaceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;
    private final RedisTemplate<String, String> redisTemplate;

    public WorkSpaceController(WorkSpaceService workSpaceService, RedisTemplate<String, String> redisTemplate) {
        this.workSpaceService = workSpaceService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/createWorkSpace")
    public ResponseEntity<?> createWorkSpace(@Valid WorkSpaceCreateDto workSpaceCreateDto) throws IOException {
        Long response = workSpaceService.saveWorkSpace(workSpaceCreateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/getRecentWorkspace")
    public ResponseEntity<Long> getRecentWorkspace(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long response = workSpaceService.getRecentWorkspaceId(userId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/setRecentWorkspace")
    public ResponseEntity<Void> setRecentWorkspace(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long workSpaceId = body.get("workSpaceId");
        workSpaceService.setRecentWorkspace(userId, workSpaceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteWorkSpace")
    public ResponseEntity<?> deleteWorkSpace(@Valid @RequestBody WorkSpaceDeleteDto workSpaceDeleteDto){
        Long workSpaceId = workSpaceDeleteDto.getWorkSpaceId();
        Long userId = workSpaceDeleteDto.getUserId();
        Long response = workSpaceService.deleteMyWorkSpace(workSpaceId,userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/getMyWorkSpace")
    public ResponseEntity<?> getMyWorkSpace(@RequestBody FindMyWorkSpace findMyWorkSpace){
        List<MyWorkSpaceResDto> response = workSpaceService.findMyWorkSpace(findMyWorkSpace.getUserId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/updateMyWorkSpace")
    public ResponseEntity<?> updateWorkSpace(@Valid WorkSpaceUpdateDto workSpaceUpdateDto) throws IOException {
        Long response = workSpaceService.updateMyWorkSpace(workSpaceUpdateDto);
        if (response == -1L) {
            return ResponseEntity.status(HttpStatus.OK).body("수정 권한이 없습니다.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/createSection")
    public ResponseEntity<?> createSection(@RequestBody SectionCreateDto sectionCreateDto){
        Long response = workSpaceService.saveSection(sectionCreateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deleteSection")
    public ResponseEntity<?> deleteSection(@RequestBody SectionDeleteDto dto){
        try {
            Long response = workSpaceService.deleteMySection(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/getMySection")
    public ResponseEntity<?> getMySection(@RequestBody FindMySectionDto findMySectionDto){
        List<MySectionResDto> response = workSpaceService.findMySection(findMySectionDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/updateMySection")
    public ResponseEntity<?> updateMySection(@RequestBody SectionupdateDto sectionupdateDto){
        Long response = workSpaceService.updateSection(sectionupdateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/createMyChannel")
    public ResponseEntity<?> createMyChannel(@RequestBody ChannelCreateDto channelCreateDto){
        Long response = workSpaceService.createChannel(channelCreateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deleteMyChannel")
    public ResponseEntity<?> deleteMyChannel(@RequestBody ChannelDeleteDto channelDeleteDto){
        Long response = workSpaceService.deleteChannel(channelDeleteDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/findMyChannel")
    public ResponseEntity<?>findMyChannel(@RequestBody FindMyChannelDto findMyChannelDto){
        List<ChannelResDto> response = workSpaceService.findChannel(findMyChannelDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/updateMyChannel")
    public ResponseEntity<?> updateMyChannel(@RequestBody ChannelUpdateDto channelUpdateDto){
        Long response = workSpaceService.updateChannel(channelUpdateDto);

        if (response == -1L) {
            return ResponseEntity.ok("수정 권한이 없습니다.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/getWorkSpaceInfo")
    public ResponseEntity<?> getWorkspaceInfo(@RequestBody GetWorkSpaceInfoDto getWorkSpaceInfoDto){
        List<WorkSpaceInfoDto> response = workSpaceService.getMyWorkspaceSectionAndChannels(getWorkSpaceInfoDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//    여러 회원을 한번에 초대하는 url
    @PostMapping("/invite")
    public ResponseEntity<String> inviteUsersToWorkspace(@RequestBody MultiWorkSpaceInviteDto dto) {
        workSpaceService.inviteUsersToWorkspace(dto.getWorkspaceId(), dto.getUserIds());
        return ResponseEntity.ok("워크스페이스에 유저들을 초대했습니다.");
    }

    @GetMapping("/invite/{inviteUrl}")
    public ResponseEntity<Long> previewInvite(@PathVariable String inviteUrl) {
        Long response = workSpaceService.getWorkspaceIdByInviteUrl(inviteUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    초대코드를 타고 들어오는 단일 회원 초대
    @PostMapping("/accept")
    public ResponseEntity<String> acceptInvite(@RequestBody InviteAcceptDto dto) throws JsonProcessingException {
        workSpaceService.acceptInvite(dto.getWorkspaceId(), dto.getUserId());
        return ResponseEntity.ok("워크스페이스에 성공적으로 참여했습니다.");
    }

    @PostMapping("/channel/invite")
    public ResponseEntity<String> inviteUserToChannel(@RequestBody ChannelInviteDto dto) {
        workSpaceService.inviteUserToChannel(dto.getChannelId(), dto.getUserIds());
        return ResponseEntity.ok("채널에 초대 완료");
    }

    @GetMapping("/participants/{workspaceId}")
    public ResponseEntity<List<Map<String, String>>> getParticipants(@PathVariable Long workspaceId) throws JsonProcessingException {
        String redisKey = "workspace:participants:" + workspaceId;
        List<String> entries = redisTemplate.opsForList().range(redisKey, 0, -1);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> result = new ArrayList<>();

        if (entries != null) {
            for (String json : entries) {
                Map<String, String> userInfo = mapper.readValue(json, new TypeReference<>() {});
                result.add(userInfo);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/workspaceParticipants/{workspaceId}")
    public ResponseEntity<?> getWorkspaceParticipants(@PathVariable Long workspaceId){
        List<WorkSpaceParticipantDto> participants = workSpaceService.getWorkspaceParticipantsFromRedis(workspaceId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/channelParticipants/{channelId}")
    public ResponseEntity<?> getChannelParticipants(@PathVariable Long channelId){
        List<Long> response = workSpaceService.getUserIdsByChannelId(channelId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
