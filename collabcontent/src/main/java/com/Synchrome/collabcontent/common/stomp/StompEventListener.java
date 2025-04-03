package com.Synchrome.collabcontent.common.stomp;

import com.Synchrome.collabcontent.common.auth.annotation.CurrentUserId;
import com.Synchrome.collabcontent.common.redis.RedisSessionManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompEventListener {

    private final RedisSessionManager sessionManager;

    public StompEventListener(RedisSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventListener
    public void connectHandle(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        sessionManager.addSession(sessionId);
        System.out.println("✅ CONNECT - sessionId: " + sessionId);
        System.out.println("Total sessions: " + sessionManager.getSessionCount());
    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        sessionManager.removeSession(sessionId);
        System.out.println("❌ DISCONNECT - sessionId: " + sessionId);
        System.out.println("Total sessions: " + sessionManager.getSessionCount());
    }
}