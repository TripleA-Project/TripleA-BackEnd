package com.triplea.triplea.core.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.dto.ResponseDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class BlackListFilter  extends OncePerRequestFilter {

    private RedisTemplate<String, String> redisTemplate;


    public BlackListFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        //쿠키에서 refreshToken 가져오기
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken != null && isTokenBlackList(refreshToken)){
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("권한 없음 : 로그아웃한 유저");
            ResponseDTO<String> responseBody = new ResponseDTO<>(HttpStatus.UNAUTHORIZED,"토큰 검증 실패","권한 없음 : 로그아웃한 유저");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), responseBody);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenBlackList(String refreshToken){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String value = values.get(refreshToken);
        if (value != null && value.equals("blackList")){
            return true;
        }else return false;
    }
}
