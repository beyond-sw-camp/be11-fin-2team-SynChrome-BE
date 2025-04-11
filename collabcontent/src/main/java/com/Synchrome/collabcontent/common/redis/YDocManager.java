package com.Synchrome.collabcontent.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class YDocManager {

    private final RedisTemplate<String, byte[]> redisTemplate;

    private static final String KEY_PREFIX = "canvas:doc:";

    public void appendUpdate(Long documentId, byte[] update) {
        String key = KEY_PREFIX + documentId;

        // Redis에 누적 저장 (마지막 업데이트로 덮어쓰기)
        // 또는 LPUSH 등으로 여러 개 누적 저장도 가능
        redisTemplate.opsForValue().set(key, update);
    }

    public byte[] getLastUpdate(Long documentId) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + documentId);
    }

    public void delete(Long documentId) {
        redisTemplate.delete(KEY_PREFIX + documentId);
    }
}
