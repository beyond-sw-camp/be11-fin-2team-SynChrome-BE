package com.Synchrome.collabcontent.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSessionManager {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisSessionManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getKey() {
        return "ws:sessions";
    }

    public void addSession(String sessionId) {
        redisTemplate.opsForSet().add(getKey(), sessionId);
        redisTemplate.expire(getKey(), 1, TimeUnit.DAYS);
    }

    public void removeSession(String sessionId) {
        redisTemplate.opsForSet().remove(getKey(), sessionId);
    }

    public Set<String> getAllSessions() {
        return redisTemplate.opsForSet().members(getKey());
    }

    public long getSessionCount() {
        return redisTemplate.opsForSet().size(getKey());
    }
}