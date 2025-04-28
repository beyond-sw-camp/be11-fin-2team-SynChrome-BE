package com.Synchrome.collabcontent.common.kafka;

import com.Synchrome.collabcontent.canvas.dto.CanvasUpdateReqDto;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.dto.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSubscriber {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat", groupId = "chat-group")
    public void listen(String messageJson) throws JsonProcessingException {
        ChatMessageDto chatMessage = objectMapper.readValue(messageJson, ChatMessageDto.class);
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getRoomId(), chatMessage);
        System.out.println("[Kafka] 메시지 수신됨: " + messageJson);
    }

    @KafkaListener(topics = "canvas", groupId = "canvas-group")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CanvasUpdateReqDto docMessage = objectMapper.readValue(message, CanvasUpdateReqDto.class);
        Long canvasId = docMessage.getCanvasId();
        System.out.println("📄 문서 ID: " + canvasId);
        messagingTemplate.convertAndSend("/topic/canvas/" + canvasId, docMessage.getUpdate());
    }

    @KafkaListener(topics = "notification", groupId = "noti-group")
    public void listenNotification(String messageJson) throws JsonProcessingException {
        NotificationDto noti = objectMapper.readValue(messageJson, NotificationDto.class);

        // 유저 전용 알림 채널로 메시지 전송
        messagingTemplate.convertAndSend("/topic/notify/" + noti.getUserId(), noti);
    }
}