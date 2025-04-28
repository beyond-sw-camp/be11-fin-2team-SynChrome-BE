package com.Synchrome.workspace.space.service;

import com.Synchrome.workspace.space.dtos.workSpaceDtos.CreateGroupRoomReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "collabcontent-service")
public interface WorkSpaceFeign {
    @PostMapping(value = "/chat/room/group/create")
    ResponseEntity<?> createGroupChatRoom(@RequestBody CreateGroupRoomReqDto requestDto);
}
