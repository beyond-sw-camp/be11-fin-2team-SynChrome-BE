package com.Synchrome.collabcontent.livechat.service;


import com.Synchrome.collabcontent.livechat.domain.LiveChat;
import com.Synchrome.collabcontent.livechat.dtos.SessionCreateDto;
import com.Synchrome.collabcontent.livechat.dtos.SessionDeleteDto;
import com.Synchrome.collabcontent.livechat.repository.LiveChatRepository;
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
