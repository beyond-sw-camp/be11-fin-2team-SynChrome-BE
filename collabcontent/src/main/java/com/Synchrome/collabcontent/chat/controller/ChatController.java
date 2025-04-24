package com.Synchrome.collabcontent.chat.controller;


import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.dto.*;
import com.Synchrome.collabcontent.chat.repository.ChatMessageRepository;
import com.Synchrome.collabcontent.chat.service.ChatService;
import com.Synchrome.collabcontent.common.auth.annotation.CurrentUserId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatService chatService, ChatMessageRepository chatMessageRepository) {
        this.chatService = chatService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(@CurrentUserId Long userId) {
        List<MyChatListResDto> myChatRooms = chatService.getMyChatRooms(userId);
        return new ResponseEntity<>(myChatRooms, HttpStatus.OK);
    }

    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupChatRoom(@RequestBody CreateGroupRoomReqDto requestDto) {
        Long roomId = chatService.createGroupChatRoom(requestDto.getUserId(), requestDto.getRoomName());
        return new ResponseEntity<>(roomId, HttpStatus.CREATED);
    }

    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@CurrentUserId Long userId,
                                                  @PathVariable Long roomId) {
        chatService.joinGroupChatRoom(userId, roomId);
        return new ResponseEntity<>("채팅방에 참여했습니다", HttpStatus.OK);
    }

    @GetMapping("/room/group/list")
    public ResponseEntity<?> getAllGroupChatRooms() {
        List<ChatRoomResDto> rooms = chatService.getAllGroupChatRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long roomId,
            @RequestParam(required = false, defaultValue = "30") int limit,
            @RequestParam(required = false) Long before) {

        List<ChatMessageDto> messages = chatService.getChatMessages(roomId, limit, before);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> readStatusUpdate(@PathVariable Long roomId, @RequestBody ReadStatusCreateDto readStatusCreateDto){
        chatService.readStatusUpdate(roomId,readStatusCreateDto);
        return new ResponseEntity<>("읽음 처리 성공", HttpStatus.OK);
    }

    @GetMapping("/thread/{parentId}")
    public ResponseEntity<?> getThreadHistory(@PathVariable Long parentId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long before){
        List<ChatMessageDto> messages =chatService.getThreadMessages(parentId, limit, before);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/message/{messageId}")
    public ChatMessageDto getSingleMessage(@PathVariable Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        return ChatMessageDto.fromEntity(message);
    }
}