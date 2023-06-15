package com.triplea.triplea.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void setValues(String refreshToken, String accessToken){
        ValueOperations<String,String> values = redisTemplate.opsForValue();
        values.set(refreshToken,accessToken, Duration.ofMinutes(1000 * 60 * 60* 24 * 7)); // 7days
    }
    public void setValuesBlackList(String refreshToken, String blackList){
        ValueOperations<String,String> values = redisTemplate.opsForValue();
        values.set(refreshToken,blackList, Duration.ofMinutes(1000 * 60 * 3)); // 3분
    }

    public String getValues(String token){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public void deleteValues(String key){
        redisTemplate.delete(key);
    }

    public boolean existsRefreshToken(String refreshToken) {
        return getValues(refreshToken) != null;
    }
}
