package com.Synchrome.collabcontent.LiveChat.Service;

import com.Synchrome.collabcontent.LiveChat.Domain.LiveChat;
import com.Synchrome.collabcontent.LiveChat.Dtos.SessionCreateDto;
import com.Synchrome.collabcontent.LiveChat.Dtos.SessionDeleteDto;
import com.Synchrome.collabcontent.LiveChat.Repository.LiveChatRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LiveChatService {
    private final LiveChatRepository liveChatRepository;

    public LiveChatService(LiveChatRepository liveChatRepository) {
        this.liveChatRepository = liveChatRepository;
    }

    public void save(SessionCreateDto dto){
        boolean exists = liveChatRepository.existsBySessionId(dto.getSessionId());
        if (!exists) {
            LiveChat liveChat = LiveChat.builder()
                    .sessionId(dto.getSessionId())
                    .build();
            liveChatRepository.save(liveChat);
        }
    }

    public Boolean delete(SessionDeleteDto dto){
        LiveChat liveChat = liveChatRepository.findBySessionId(dto.getSessionId()).orElseThrow(()->new EntityNotFoundException("없음"));
        liveChat.liveChatStop();
        return true;
    }
}
