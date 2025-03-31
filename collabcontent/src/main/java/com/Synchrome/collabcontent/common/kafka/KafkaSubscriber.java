package com.Synchrome.collabcontent.common.kafka;

import com.Synchrome.collabcontent.canvas.dto.DocumentMessageDto;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
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

    @KafkaListener(topics = "document", groupId = "doc-group")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        DocumentMessageDto docMessage = objectMapper.readValue(message, DocumentMessageDto.class);
        String documentId = docMessage.getDocumentId();
        System.out.println("📄 문서 ID: " + documentId);
        System.out.println("인코딩 메시지 : " + docMessage.getUpdate());
        messagingTemplate.convertAndSend("/topic/document/" + documentId, docMessage.getUpdate());
    }
}