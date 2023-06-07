package com.triplea.triplea.service;

import com.triplea.triplea.model.token.RefreshTokenRepository;
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

    public void setValues(String token, String email){
        ValueOperations<String,String> values = redisTemplate.opsForValue();
        values.set(token,email, Duration.ofMinutes(1000 * 60 * 60* 24 * 7)); // 7days
    }

    public String getValues(String token){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public void deleteValues(String token){
        redisTemplate.delete(token.substring(7));
    }

    public boolean existsRefreshToken(String refreshToken) {
        return getValues(refreshToken) != null;
        //// return tokenRepository.existsByRefreshToken(refreshToken);
    }
}
