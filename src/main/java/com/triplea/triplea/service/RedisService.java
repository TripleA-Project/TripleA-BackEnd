package com.triplea.triplea.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void setValues(String refreshToken, String userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(userId, refreshToken, Duration.ofMinutes(1000 * 60 * 60 * 24 * 7)); // 7days
    }

    public void setValuesBlackList(String AccessToken, String blackList) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(AccessToken, blackList, Duration.ofMinutes(1000 * 60 * 3)); // 5분
    }

    public String getValues(String userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(userId);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public boolean existsRefreshToken(String userId) {
        return getValues(userId) != null;
    }
}
