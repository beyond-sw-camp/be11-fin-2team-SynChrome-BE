package com.Synchrome.collabcontent.common.stomp;


import com.Synchrome.collabcontent.canvas.dto.CanvasMessageDto;
import com.Synchrome.collabcontent.canvas.service.CanvasService;
import com.Synchrome.collabcontent.chat.domain.ChatMessage;
import com.Synchrome.collabcontent.chat.dto.ChatMessageDto;
import com.Synchrome.collabcontent.chat.service.ChatService;
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
    private final CanvasService canvasService;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService, KafkaTemplate kafkaTemplate, CanvasService canvasService) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
        this.kafkaTemplate = kafkaTemplate;
        this.canvasService = canvasService;
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

    @MessageMapping("/canvas/{canvasId}")
    public void handleCanvasUpdate(@DestinationVariable Long canvasId, CanvasMessageDto canvasMessageDto) throws JsonProcessingException {
        canvasService.saveCanvas(canvasMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(canvasMessageDto);
        System.out.println("✅ 캔버스 변경 사항 수신");
        kafkaTemplate.send("canvas", message);
    }
}