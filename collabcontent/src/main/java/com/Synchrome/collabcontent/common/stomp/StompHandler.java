package com.Synchrome.collabcontent.common.stomp;


import com.Synchrome.collabcontent.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secretKey}")
    private String secretKey;
    private final ChatService chatService;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("connect요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
//            토큰 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료");
        }

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            System.out.println("subscribe 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = Long.parseLong(claims.getSubject());
            String destination = accessor.getDestination();

            // ✅ 채팅 구독일 경우
            if (destination.startsWith("/topic/chat/")) {
                String roomId = destination.split("/")[3]; // 예: /topic/chat/123
                System.out.println("채팅방 구독 : " + roomId);
                if (!chatService.isRoomParticipant(Long.parseLong(roomId), userId)) {
                    throw new IllegalArgumentException("해당 채팅방에 권한이 없습니다.");
                }
            }

            // ✅ 문서 구독일 경우
            else if (destination.startsWith("/topic/document/")) {
                String docId = destination.split("/")[3]; // 예: /topic/document/doc-123
                // 👉 문서 접근 권한 확인 (DocumentService 등에서 확인 필요)
                // 예시: if (!documentService.hasAccess(userId, docId)) { ... }
                System.out.println("문서 구독 권한 검사: docId = " + docId + ", user = " + userId);
            }
        }

        return message;
    }

}