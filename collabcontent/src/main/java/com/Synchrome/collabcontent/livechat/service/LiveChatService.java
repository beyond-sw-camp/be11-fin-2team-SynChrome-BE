package com.Synchrome.collabcontent.livechat.service;



import com.Synchrome.collabcontent.livechat.domain.LiveChat;
import com.Synchrome.collabcontent.livechat.domain.Participants;
import com.Synchrome.collabcontent.livechat.dtos.ParticipantAddDto;
import com.Synchrome.collabcontent.livechat.dtos.SessionCreateDto;
import com.Synchrome.collabcontent.livechat.dtos.SessionDeleteDto;
import com.Synchrome.collabcontent.livechat.repository.LiveChatRepository;
import com.Synchrome.collabcontent.livechat.repository.ParticipantsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LiveChatService {
    private final LiveChatRepository liveChatRepository;
    private final ParticipantsRepository participantsRepository;

    public LiveChatService(LiveChatRepository liveChatRepository, ParticipantsRepository participantsRepository) {
        this.liveChatRepository = liveChatRepository;
        this.participantsRepository = participantsRepository;
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

    public Participants participants(ParticipantAddDto dto){
        LiveChat liveChat = liveChatRepository.findBySessionId(dto.getSessionId()).orElseThrow(()->new EntityNotFoundException("없는 회의방입니다."));
        Participants participant = Participants.builder().liveChat(liveChat).userId(dto.getUserId()).build();
        Participants addParticipant = participantsRepository.save(participant);
        return addParticipant;
    }
}
