package com.triplea.triplea.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.triplea.triplea.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyJwtProvider {

    private static String SUBJECT;
    private static final int EXP = 1000 * 60 * 60; // 5분
    private static final int REFRESH_EXP = 1000 * 60 * 60 * 24 * 14; // 일주일
    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    public static final String HEADER = "Authorization";
    private static String SECRET;

    @Value("${jwt.subject}")
    private void setSUBJECT(String subject) {
        SUBJECT = subject;
    }

    @Value("${jwt.secret}")
    private void setSECRET(String secret) {
        SECRET = secret;
    }


    public static String createAccessToken(User user) {

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getMemberRole().toString())
                .sign(Algorithm.HMAC512(SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public String createRefreshToken(User user) {
        String refreshToken = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getMemberRole().toString())
                .sign(Algorithm.HMAC512(SECRET));
        return refreshToken;
    }

    public String recreationAccessToken(User user) {

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getMemberRole().toString())
                .sign(Algorithm.HMAC512(SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(jwt);
        return decodedJWT;
    }
}