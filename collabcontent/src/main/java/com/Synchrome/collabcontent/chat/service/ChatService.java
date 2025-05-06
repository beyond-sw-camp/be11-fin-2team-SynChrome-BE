package com.Synchrome.collabcontent.chat.service;


import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.domain.ChatParticipant;
import com.Synchrome.collabcontent.chat.domain.ChatRoom;
import com.Synchrome.collabcontent.chat.domain.ReadStatus;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.dto.ChatRoomResDto;
import com.Synchrome.collabcontent.chat.dto.MyChatListResDto;
import com.Synchrome.collabcontent.chat.dto.ReadStatusCreateDto;
import com.Synchrome.collabcontent.chat.repository.ChatMessageRepository;
import com.Synchrome.collabcontent.chat.repository.ChatParticipantRepository;
import com.Synchrome.collabcontent.chat.repository.ChatRoomRepository;
import com.Synchrome.collabcontent.chat.repository.ReadStatusRepository;
import com.Synchrome.collabcontent.common.domain.DelYN;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            Long roomId = room.getId();

            // 해당 유저의 ReadStatus 가져오기
            ReadStatus readStatus = readStatusRepository
                    .findByUserIdAndChatRoomId(userId, roomId)
                    .orElse(null);

            boolean hasUnread = false;

            if (readStatus != null) {
                // 해당 시점 이후 메시지 있는지 확인
                hasUnread = chatMessageRepository.existsByChatRoomIdAndIdGreaterThan(
                        roomId, readStatus.getLastReadMessageId());
            } else {
                // 처음 들어간 채팅방이거나 읽음 처리 전 => 모두 안읽음 처리
                hasUnread = chatMessageRepository.existsByChatRoomId(roomId);
            }

            return MyChatListResDto.builder()
                    .roomId(roomId)
                    .roomName(room.getName())
                    .isGroupChat(room.getIsGroupChat())
                    .presentUnreadMessage(hasUnread)
                    .build();
        }).collect(Collectors.toList());
    }


    public void leaveGroupChat(Long userId, Long roomId) {
        ChatParticipant participant = chatParticipantRepository
                .findByUserIdAndChatRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 참가자 정보를 찾을 수 없습니다."));
        chatParticipantRepository.delete(participant);
    }

    public Long createGroupChatRoom(Long userId, String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();
        Long roomId = chatRoomRepository.save(chatRoom).getId();

        ChatParticipant creator = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .userId(userId)
                .build();
        chatParticipantRepository.save(creator);
        return roomId;
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
                .replyTo(dto.getReplyTo())
                .replyPreview(dto.getReplyPreview())
                .build();
        if (dto.getParentId() != null) {
            chatMessageRepository.findById(dto.getParentId())
                    .orElseThrow(()->new IllegalArgumentException("없는 메시지 입니다"))
                    .addThreadCount();
        }
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getChatMessages(Long roomId, int limit, Long beforeId) {
        List<ChatMessage> messages;

        if (beforeId == null) {
            // 최신 메시지부터
            messages = chatMessageRepository.findByChatRoomIdAndParentIdIsNullAndDelYNOrderByCreatedTimeDesc(roomId,  DelYN.N, PageRequest.of(0, limit));
        } else {
            // 특정 메시지보다 이전 메시지
            LocalDateTime beforeTime = chatMessageRepository.findById(beforeId).get().getCreatedTime();
            messages = chatMessageRepository.findByChatRoomIdAndCreatedTimeLessThanAndParentIdIsNullAndDelYNOrderByCreatedTimeDesc(roomId, beforeTime, DelYN.N, PageRequest.of(0, limit));
        }
        System.out.println(messages);
        return messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .userId(m.getUserId())
                        .roomId(roomId)
                        .message(m.getContent())
                        .createdTime(m.getCreatedTime())
                        .parentId(m.getParentId())
                        .totalThreadCount(m.getTotalThreadCount())
                        .replyTo(m.getReplyTo())
                        .replyPreview(m.getReplyPreview())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean isRoomParticipant(Long roomId, Long userId){
        return chatParticipantRepository.findByUserIdAndChatRoomId(userId, roomId).isPresent();
    }

    public void readStatusUpdate(Long roomId, ReadStatusCreateDto dto) {
        ReadStatus myReadStatus = ReadStatus.builder().chatRoomId(roomId).userId(dto.getUserId()).lastReadMessageId(dto.getLastReadMessageId()).build();
        ReadStatus readStatus = readStatusRepository.save(myReadStatus);
    }

    public List<ChatMessageDto> getThreadMessages(Long parentId, int limit, Long beforeId) {
        List<ChatMessage> messages;

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));

        if (beforeId == null) {
            // 최신 메시지부터
            messages = chatMessageRepository.findByParentIdAndDelYNOrderByIdDesc(parentId, DelYN.N, pageable);
        } else {
            // 특정 메시지 ID보다 이전
            messages = chatMessageRepository.findByParentIdAndDelYNAndIdLessThanOrderByIdDesc(parentId, beforeId, DelYN.N, pageable);
        }

        return messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .userId(m.getUserId())
                        .roomId(m.getChatRoom().getId())
                        .message(m.getContent())
                        .createdTime(m.getCreatedTime())
                        .parentId(m.getParentId())
                        .replyTo(m.getReplyTo())
                        .replyPreview(m.getReplyPreview())
                        .build())
                .collect(Collectors.toList());
    }

    public ChatMessage modifyMessage(ChatMessageDto chatMessageDto){
        ChatMessage chatMessage = chatMessageRepository.findById(chatMessageDto.getId()).orElseThrow(()->new IllegalArgumentException("없는 메세지 입니다"));
        chatMessage.modifyContent(chatMessageDto.getMessage());
        return chatMessage;
    }

    public void deleteMessage(ChatMessageDto chatMessageDto){
        ChatMessage chatMessage = chatMessageRepository.findById(chatMessageDto.getId()).orElseThrow(()->new IllegalArgumentException("없는 메세지 입니다"));
        if (chatMessageDto.getParentId() != null) {
            chatMessageRepository.findById(chatMessageDto.getParentId())
                    .orElseThrow(()->new IllegalArgumentException("없는 메시지 입니다"))
                    .decreaseThreadCount();
        }
        chatMessage.deleteMessage(DelYN.Y);
    }
}
