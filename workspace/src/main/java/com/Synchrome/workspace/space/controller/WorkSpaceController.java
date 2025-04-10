package com.Synchrome.workspace.space.controller;

import com.Synchrome.workspace.space.dtos.channelDtos.*;
import com.Synchrome.workspace.space.dtos.sectionDtos.*;
import com.Synchrome.workspace.space.dtos.workSpaceDtos.*;
import com.Synchrome.workspace.space.service.WorkSpaceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;

    public WorkSpaceController(WorkSpaceService workSpaceService) {
        this.workSpaceService = workSpaceService;
    }

    @PostMapping("/createWorkSpace")
    public ResponseEntity<?> createWorkSpace(@Valid WorkSpaceCreateDto workSpaceCreateDto) throws IOException {
        Long response = workSpaceService.saveWorkSpace(workSpaceCreateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<?> updateWorkSpace(@Valid WorkSpaceUpdateDto workSpaceUpdateDto){
        Long response = workSpaceService.updateMyWorkSpace(workSpaceUpdateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("inviteUser")
    public ResponseEntity<?> inviteUser(@RequestBody InviteUserDto inviteUserDto){
        String response = workSpaceService.inviteWorkSpace(inviteUserDto);
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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/getWorkSpaceInfo")
    public ResponseEntity<?> getWorkspaceInfo(@RequestBody GetWorkSpaceInfoDto getWorkSpaceInfoDto){
        List<WorkSpaceInfoDto> response = workSpaceService.getMyWorkspaceSectionAndChannels(getWorkSpaceInfoDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
