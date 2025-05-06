package com.Synchrome.collabcontent.livechat.service;



import com.Synchrome.collabcontent.livechat.domain.IsEnded;
import com.Synchrome.collabcontent.livechat.domain.LiveChat;
import com.Synchrome.collabcontent.livechat.domain.Participants;
import com.Synchrome.collabcontent.livechat.dtos.*;
import com.Synchrome.collabcontent.livechat.repository.LiveChatRepository;
import com.Synchrome.collabcontent.livechat.repository.ParticipantsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
                    .channelId(dto.getChannelId())
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

    public SessionIdResDto findSession(FindSessionIdDto dto){
        return liveChatRepository.findTopByChannelIdAndIsEndedOrderByCreatedTimeDesc(dto.getChannelId(), IsEnded.N)
                .map(liveChat -> SessionIdResDto.builder()
                        .sessionId(liveChat.getSessionId())
                        .build())
                .orElse(SessionIdResDto.builder()
                        .sessionId(null) // ❗ 세션 없으면 null 주기
                        .build());
    }

    public Long deleteParticipant(ParticipantDeleteDto dto) {
        Participants participant = participantsRepository
                .findByUserIdAndLiveChat_SessionId(dto.getUserId(), dto.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 세션에 참가하지 않은 사용자입니다."));

        participant.deleteParticipant();
        return participant.getUserId();
    }

    public boolean isUserJoined(String sessionId, Long userId) {
        Optional<Participants> participantOpt = participantsRepository.findByUserIdAndLiveChat_SessionId(userId, sessionId);
        return participantOpt.isPresent() && participantOpt.get().getIsEnded() == IsEnded.N;
    }
}
