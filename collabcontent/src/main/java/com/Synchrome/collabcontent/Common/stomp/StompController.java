package com.Synchrome.collabcontent.Common.stomp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    private final KafkaTemplate kafkaTemplate;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService, KafkaTemplate kafkaTemplate) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageReqDto) throws JsonProcessingException {
        ChatMessage chatMessage = chatService.saveMessage(roomId, chatMessageReqDto);
        chatMessageReqDto.setId(chatMessage.getId());
        chatMessageReqDto.setCreatedTime(chatMessage.getCreatedTime());
        chatMessageReqDto.setRoomId(roomId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ 이 줄 추가
        String message = objectMapper.writeValueAsString(chatMessageReqDto);
        System.out.println("✅ 카프카 프로듀서 실행");
        kafkaTemplate.send("chat", message);
    }

    @MessageMapping("/document/{documentId}")  // 실제 경로: /publish/document/1
    public void handleDocumentUpdate(@DestinationVariable Long documentId, DocumentMessageDto documentMessageDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(documentMessageDto);
        System.out.println("✅ 캔버스 변경 사항 수신");
        kafkaTemplate.send("document", message);
    }
}