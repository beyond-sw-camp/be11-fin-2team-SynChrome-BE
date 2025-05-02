package com.Synchrome.collabcontent.chat.service;

import com.Synchrome.collabcontent.chat.domain.ENUM.Del;
import com.Synchrome.collabcontent.chat.domain.Emotion;
import com.Synchrome.collabcontent.chat.dto.EmotionDetailDto;
import com.Synchrome.collabcontent.chat.dto.MessageEmotionDto;
import com.Synchrome.collabcontent.chat.repository.EmotionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmotionService {
    @Qualifier("emotionDB")
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmotionRepository emotionRepository;

    public EmotionService(RedisTemplate<String, Object> redisTemplate, EmotionRepository emotionRepository) {
        this.redisTemplate = redisTemplate;
        this.emotionRepository = emotionRepository;
    }

    public void toggleAndSave(Long roomId, Long messageId, String emoji, Long userId) {
        String redisKey = "emoji:" + roomId + ":" + messageId + ":" + emoji;
        String userIdStr = String.valueOf(userId);

        Boolean isMember = redisTemplate.opsForSet().isMember(redisKey, userIdStr);

        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(redisKey, userIdStr);
            emotionRepository.save(Emotion.builder()
                    .roomId(roomId)
                    .messageId(messageId)
                    .userId(userId)
                    .emotion(emoji)
                    .del(Del.Y)
                    .build());
        } else {
            redisTemplate.opsForSet().add(redisKey, userIdStr);
            redisTemplate.expire(redisKey, Duration.ofHours(24));
            emotionRepository.save(Emotion.builder()
                    .roomId(roomId)
                    .messageId(messageId)
                    .userId(userId)
                    .emotion(emoji)
                    .del(Del.N)
                    .build());
        }
    }



    public Long getEmotionCount(Long roomId, Long messageId, String emoji) {
        String redisKey = "emoji:" + roomId + ":" + messageId + ":" + emoji;
        Long size = redisTemplate.opsForSet().size(redisKey);
        return size != null ? size : 0L;
    }


    public List<MessageEmotionDto> getEmojiDetailsForRoom(Long roomId) {
        String redisPrefix = "emoji:" + roomId + ":";
        Set<String> redisKeys = redisTemplate.keys(redisPrefix + "*");

        if (redisKeys != null && !redisKeys.isEmpty()) {
            return buildMessageEmotionDtoFromRedis(redisKeys, redisPrefix);
        }

        // 캐시가 없을 경우 DB에서 가져와 캐싱
        List<Emotion> emotions = emotionRepository.findAllByRoomIdAndDel(roomId, Del.N);
        for (Emotion e : emotions) {
            String key = redisPrefix + e.getMessageId() + ":" + e.getEmotion();
            redisTemplate.opsForSet().add(key, String.valueOf(e.getUserId()));
        }

        Set<String> newRedisKeys = redisTemplate.keys(redisPrefix + "*");
        return buildMessageEmotionDtoFromRedis(newRedisKeys, redisPrefix);
    }

    private List<MessageEmotionDto> buildMessageEmotionDtoFromRedis(Set<String> keys, String redisPrefix) {
        Map<Long, List<EmotionDetailDto>> messageMap = new HashMap<>();

        for (String key : keys) {
            String stripped = key.replace(redisPrefix, ""); // ex: "101:😂"
            String[] parts = stripped.split(":");
            if (parts.length != 2) continue;

            Long messageId = Long.valueOf(parts[0]);
            String emoji = parts[1];

            Set<Object> rawUserIds = redisTemplate.opsForSet().members(key);
            if (rawUserIds == null) continue;

            Set<String> userIds = rawUserIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());


            messageMap.computeIfAbsent(messageId, k -> new ArrayList<>())
                    .add(new EmotionDetailDto(emoji, new ArrayList<>(userIds), (long) userIds.size()));
        }

        return messageMap.entrySet().stream()
                .map(e -> new MessageEmotionDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }




}
