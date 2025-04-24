package com.Synchrome.collabcontent.chat.dto;

import lombok.Getter;

@Getter
public class CreateGroupRoomReqDto {
    private Long userId;
    private String roomName;
}