package com.Synchrome.collabcontent.chat.controller;


import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.dto.ChatRoomResDto;
import com.Synchrome.collabcontent.chat.dto.CreateGroupRoomReqDto;
import com.Synchrome.collabcontent.chat.dto.MyChatListResDto;
import com.Synchrome.collabcontent.chat.service.ChatService;
import com.Synchrome.collabcontent.common.auth.annotation.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/my/rooms")
    public ResponseEntity<List<MyChatListResDto>> getMyChatRooms(@CurrentUserId Long userId) {
        return ResponseEntity.ok(chatService.getMyChatRooms(userId));
    }

    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<Void> leaveGroupChat(@CurrentUserId Long userId,
                                               @PathVariable Long roomId) {
        chatService.leaveGroupChat(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/group/create")
    public ResponseEntity<Void> createGroupChatRoom(@CurrentUserId Long userId,
                                                    @RequestBody CreateGroupRoomReqDto requestDto) {
        chatService.createGroupChatRoom(userId, requestDto.getRoomName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<Void> joinGroupChatRoom(@CurrentUserId Long userId,
                                                  @PathVariable Long roomId) {
        chatService.joinGroupChatRoom(userId, roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/group/list")
    public ResponseEntity<List<ChatRoomResDto>> getAllGroupChatRooms() {
        return ResponseEntity.ok(chatService.getAllGroupChatRooms());
    }

    @GetMapping("/chat/history/{roomId}")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long roomId,
            @RequestParam(required = false, defaultValue = "30") int limit,
            @RequestParam(required = false) Long before) {

        List<ChatMessageDto> messages = chatService.getChatMessages(roomId, limit, before);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}