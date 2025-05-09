package com.Synchrome.collabcontent.common.stomp;


import com.Synchrome.collabcontent.canvas.dto.CanvasUpdateReqDto;
import com.Synchrome.collabcontent.canvas.service.CanvasService;
import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.domain.ENUM.NotificationType;
import com.Synchrome.collabcontent.chat.domain.Emotion;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.dto.NotificationDto;
import com.Synchrome.collabcontent.chat.service.ChatService;
import com.Synchrome.collabcontent.chat.service.EmotionService;
import com.Synchrome.collabcontent.chat.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    private final KafkaTemplate kafkaTemplate;
    private final CanvasService canvasService;
    private final NotificationService notificationService;
    private final EmotionService emotionService;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService, KafkaTemplate kafkaTemplate, CanvasService canvasService, NotificationService notificationService, EmotionService emotionService) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
        this.kafkaTemplate = kafkaTemplate;
        this.canvasService = canvasService;
        this.notificationService = notificationService;
        this.emotionService = emotionService;
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageReqDto) throws JsonProcessingException {
        if(chatMessageReqDto.getType().equals("typing") || chatMessageReqDto.getType().equals("stopTyping")){
            chatMessageReqDto.setRoomId(roomId);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // ✅ 이 줄 추가
            String message = objectMapper.writeValueAsString(chatMessageReqDto);
            System.out.println("✅ 카프카 프로듀서 실행");
            kafkaTemplate.send("chat", message);
        }
        else if (chatMessageReqDto.getType().equals("emotion")) {
            Long messageId = chatMessageReqDto.getParentId();
            String emoji = chatMessageReqDto.getMessage();
            Long userId = chatMessageReqDto.getUserId();

            // ✅ roomId도 함께 전달
            emotionService.toggleAndSave(roomId, messageId, emoji, userId);

            // ✅ Redis count 조회도 roomId 포함
            Long count = emotionService.getEmotionCount(roomId, messageId, emoji);

            chatMessageReqDto.setEmotionSize(count);
            chatMessageReqDto.setRoomId(roomId);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String emotionMessage = objectMapper.writeValueAsString(chatMessageReqDto);

            kafkaTemplate.send("chat", emotionMessage);
        }

        else if(chatMessageReqDto.getType().equals("modify")){
            ChatMessage chatMessage = chatService.modifyMessage(chatMessageReqDto);
            chatMessageReqDto.setUpdatedTime(chatMessage.getUpdatedTime());
            chatMessageReqDto.setRoomId(roomId);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(chatMessageReqDto);
            System.out.println("✅ 카프카 프로듀서 실행");
            kafkaTemplate.send("chat", message);
        }

        else if(chatMessageReqDto.getType().equals("delete")){
            chatMessageReqDto.setRoomId(roomId);
            chatService.deleteMessage(chatMessageReqDto);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(chatMessageReqDto);
            System.out.println("✅ 카프카 프로듀서 실행");
            kafkaTemplate.send("chat", message);
        }

        else if(chatMessageReqDto.getType().equals("message")) {
            ChatMessage chatMessage = chatService.saveMessage(roomId, chatMessageReqDto);
            chatMessageReqDto.setId(chatMessage.getId());
            chatMessageReqDto.setCreatedTime(chatMessage.getCreatedTime());
            chatMessageReqDto.setRoomId(roomId);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // ✅ 이 줄 추가
            String message = objectMapper.writeValueAsString(chatMessageReqDto);
            System.out.println("✅ 카프카 프로듀서 실행");
            kafkaTemplate.send("chat", message);

            if (chatMessageReqDto.getMentionedUserIds() != null) {
                for (Long mentionedUserId : chatMessageReqDto.getMentionedUserIds()) {
                    // DB 저장
                    notificationService.saveNotification(
                            mentionedUserId,
                            chatMessageReqDto.getUserId(),
                            roomId,
                            chatMessageReqDto.getMessage(),
                            chatMessage.getWorkspaceId(),
                            chatMessage.getId(),
                            NotificationType.MENTION, // ✅
                            chatMessage.getWorkspaceTitle()
                    );

                    // WebSocket 알림 전송
                    NotificationDto notification = new NotificationDto();
                    notification.setUserId(mentionedUserId);
                    notification.setRoomId(roomId);
                    notification.setFromUserId(chatMessageReqDto.getUserId());
                    notification.setMessage(chatMessageReqDto.getMessage());
                    notification.setType(NotificationType.MENTION);

                    String notificationJson = objectMapper.writeValueAsString(notification);
                    kafkaTemplate.send("notification", notificationJson);
                }
            }
        }
    }

    @MessageMapping("/canvas/{canvasId}")
    public void handleCanvasUpdate(@DestinationVariable Long canvasId, CanvasUpdateReqDto canvasMessageDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(canvasMessageDto);
        System.out.println("✅ 캔버스 변경 사항 수신");
        kafkaTemplate.send("canvas", message);
    }
}