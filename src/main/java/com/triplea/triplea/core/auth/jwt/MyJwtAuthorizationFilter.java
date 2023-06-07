package com.triplea.triplea.core.auth.jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception401;
import com.triplea.triplea.core.util.MyFilterResponseUtil;
import com.triplea.triplea.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.model.user.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyJwtAuthorizationFilter extends BasicAuthenticationFilter {

    public MyJwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String prefixJwt = request.getHeader(MyJwtProvider.HEADER);

        if (prefixJwt == null) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = prefixJwt.replace(MyJwtProvider.TOKEN_PREFIX, "");
        try {
            System.out.println("디버그 : 토큰 있음");
            DecodedJWT decodedJWT = MyJwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String email = decodedJWT.getClaim("email").asString();

            User user = User.builder().id(id).email(email).build();
            MyUserDetails myUserDetails = new MyUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            myUserDetails.getUsername(),
                            myUserDetails.getPassword(),
                            myUserDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("디버그 : 인증 객체 만들어짐");
        } catch (SignatureVerificationException sve) {
            log.error("토큰 검증 실패");
            // Create the response body
            ResponseDTO<String> responseBody = new ResponseDTO<>(HttpStatus.UNAUTHORIZED,"토큰 검증 실패","토큰 검증 실패");

            // Set the response content type
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Write the response body to the servlet response
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), responseBody);

            // Return without invoking the rest of the filter chain
            return;
        } catch (TokenExpiredException tee) {
            log.error("토큰 만료됨");
            // Create the response body
            ResponseDTO<String> responseBody = new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "토큰 만료됨", "토큰 만료됨");

            // Set the response content type
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Write the response body to the servlet response
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), responseBody);

            // Return without invoking the rest of the filter chain
            return;
        }finally {
            chain.doFilter(request, response);
        }
    }

}