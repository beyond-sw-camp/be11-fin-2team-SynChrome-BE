package com.Synchrome.collabcontent.chat.service;


import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.domain.ChatParticipant;
import com.Synchrome.collabcontent.chat.domain.ChatRoom;
import com.Synchrome.collabcontent.chat.domain.ReadStatus;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.dto.ChatRoomResDto;
import com.Synchrome.collabcontent.chat.dto.MyChatListResDto;
import com.Synchrome.collabcontent.chat.repository.ChatMessageRepository;
import com.Synchrome.collabcontent.chat.repository.ChatParticipantRepository;
import com.Synchrome.collabcontent.chat.repository.ChatRoomRepository;
import com.Synchrome.collabcontent.chat.repository.ReadStatusRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readStatusRepository = readStatusRepository;
    }

    public List<MyChatListResDto> getMyChatRooms(Long userId) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserId(userId);

        return participants.stream().map(cp -> {
            ChatRoom room = cp.getChatRoom();
            Long unreadCount = readStatusRepository.countByChatRoomAndUserIdAndIsReadFalse(room, userId);
            return MyChatListResDto.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .isGroupChat(room.getIsGroupChat())
                    .unReadCount(unreadCount)
                    .build();
        }).collect(Collectors.toList());
    }


    public void leaveGroupChat(Long userId, Long roomId) {
        ChatParticipant participant = chatParticipantRepository
                .findByUserIdAndChatRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 참가자 정보를 찾을 수 없습니다."));
        chatParticipantRepository.delete(participant);
    }

    public void createGroupChatRoom(Long userId, String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);

        ChatParticipant creator = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .userId(userId)
                .build();
        chatParticipantRepository.save(creator);
    }

    public void joinGroupChatRoom(Long userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        boolean alreadyJoined = chatParticipantRepository
                .findByUserIdAndChatRoomId(userId, roomId)
                .isPresent();
        if (!alreadyJoined) {
            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    .userId(userId)
                    .build();
            chatParticipantRepository.save(participant);
        }
    }

    public List<ChatRoomResDto> getAllGroupChatRooms() {
        return chatRoomRepository.findByIsGroupChat("Y").stream()
                .map(room -> new ChatRoomResDto(room.getId(), room.getName()))
                .collect(Collectors.toList());
    }
    public ChatMessage saveMessage(Long roomId, ChatMessageDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .userId(dto.getUserId())
                .content(dto.getMessage())
                .parentId(dto.getParentId()) // null 허용
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getChatMessages(Long roomId, int limit, Long beforeId) {
        List<ChatMessage> messages;

        if (beforeId == null) {
            // 최신 메시지부터
            messages = chatMessageRepository.findTopByChatRoomIdAndParentIdIsNullOrderByIdDesc(roomId, PageRequest.of(0, limit));
        } else {
            // 특정 메시지보다 이전 메시지
            messages = chatMessageRepository.findByChatRoomIdAndIdLessThanAndParentIdIsNullOrderByIdDesc(roomId, beforeId, PageRequest.of(0, limit));
        }

        return messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .userId(m.getUserId())
                        .roomId(roomId)
                        .message(m.getContent())
                        .createdTime(m.getCreatedTime())
                        .parentId(m.getParentId())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean isRoomParticipant(Long roomId, Long userId){
        return chatParticipantRepository.findByUserIdAndChatRoomId(userId, roomId).isPresent();
    }

    public void readStatusUpdate(Long roomId, Long userId) {
        List<ReadStatus> unreadStatuses = readStatusRepository.findAllByChatRoomIdAndUserIdAndIsReadFalse(roomId, userId);
        unreadStatuses.forEach(status -> status.updateIsRead(true));
        readStatusRepository.saveAll(unreadStatuses);
    }

    public List<ChatMessageDto> getThreadMessages(Long parentId, int limit, Long beforeId) {
        List<ChatMessage> messages;

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));

        if (beforeId == null) {
            // 최신 메시지부터
            messages = chatMessageRepository.findByParentIdOrderByIdDesc(parentId, pageable);
        } else {
            // 특정 메시지 ID보다 이전
            messages = chatMessageRepository.findByParentIdAndIdLessThanOrderByIdDesc(parentId, beforeId, pageable);
        }

        return messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .userId(m.getUserId())
                        .roomId(m.getChatRoom().getId())
                        .message(m.getContent())
                        .createdTime(m.getCreatedTime())
                        .parentId(m.getParentId())
                        .build())
                .collect(Collectors.toList());
    }

}
