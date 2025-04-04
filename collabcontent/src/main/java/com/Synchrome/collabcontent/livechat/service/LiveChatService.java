package com.Synchrome.collabcontent.liveChat.Service;


import com.Synchrome.collabcontent.liveChat.Domain.LiveChat;
import com.Synchrome.collabcontent.liveChat.Domain.Participants;
import com.Synchrome.collabcontent.liveChat.Dtos.ParticipantAddDto;
import com.Synchrome.collabcontent.liveChat.Dtos.SessionCreateDto;
import com.Synchrome.collabcontent.liveChat.Dtos.SessionDeleteDto;
import com.Synchrome.collabcontent.liveChat.Repository.LiveChatRepository;
import com.Synchrome.collabcontent.liveChat.Repository.ParticipantsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Part;
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
