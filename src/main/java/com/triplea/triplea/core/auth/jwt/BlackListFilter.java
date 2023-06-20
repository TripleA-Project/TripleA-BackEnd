package com.triplea.triplea.core.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.dto.ResponseDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class BlackListFilter extends OncePerRequestFilter {

    private RedisTemplate<String, String> redisTemplate;


    public BlackListFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && isTokenBlackList(accessToken)) {
            ResponseDTO<String> responseBody = new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "토큰 검증 실패", "권한 없음 : 로그아웃한 유저");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            PrintWriter writer = response.getWriter(); // 출력 스트림 저장

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(writer, responseBody);
            } finally {
                writer.flush(); // 출력 스트림 비우기
                writer.close(); // 출력 스트림 닫기
            }

            return; // 필터 체인 중단
        }

        filterChain.doFilter(request, response);
    }


    private boolean isTokenBlackList(String accessToken) {
        accessToken = accessToken.replace("Bearer ", "");
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String value = values.get(accessToken);
        if (value != null && value.equals("blackList")) {
            return true;
        } else return false;
    }
}
