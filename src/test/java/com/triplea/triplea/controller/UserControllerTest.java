package com.triplea.triplea.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.core.config.RedisConfig;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.RedisService;
import com.triplea.triplea.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RedisService redisService;

    @MockBean
    private RedisConfig redisConfig;

    private final MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    private final User user = User.builder()
            .id(1L)
            .email("test@example.com")
            .password("123456")
            .fullName("tester")
            .newsLetter(true)
            .emailVerified(true)
            .userAgent("Custom User Agent")
            .clientIP("127.0.0.1")
            .profile("profile1")
            .build();

    @Test
    @DisplayName("회원가입")
    void join() throws Exception {
        //given
        UserRequest.Join join = UserRequest.Join.builder()
                .email("test@example.com")
                .password("123456")
                .passwordCheck("123456")
                .fullName("tester")
                .newsLetter(true)
                .emailVerified(true)
                .build();
        ObjectMapper om = new ObjectMapper();
        String requestBody = om.writeValueAsString(join);

        //when then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(contentType)
                        .content(requestBody)
                        .header("User-Agent", "Custom User Agent")
                        .with(request -> {
                            request.setRemoteAddr("127.0.0.1");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("이메일 인증 요청")
    void email() throws Exception {
        //given
        UserRequest.EmailSend email = new UserRequest.EmailSend("test@example.com");
        ObjectMapper om = new ObjectMapper();
        String requestBody = om.writeValueAsString(email);
        given(userService.email(any())).willReturn("code");

        //when then
        mockMvc.perform(post("/api/email")
                        .with(csrf())
                        .contentType(contentType)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data", is("code")))
                .andReturn();
    }

    @Test
    @DisplayName("이메일 인증 확인")
    void emailVerified() throws Exception {
        //given
        UserRequest.EmailVerify email = new UserRequest.EmailVerify("test@example.com", "code");
        ObjectMapper om = new ObjectMapper();
        String requestBody = om.writeValueAsString(email);

        //when then
        mockMvc.perform(post("/api/email/verify")
                        .with(csrf())
                        .contentType(contentType)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("구독")
    void subscribe() throws Exception {
        //given
        String accessToken = MyJwtProvider.createAccessToken(user);
        //when
        String url = "https://example.com";
        when(userService.subscribe(any(User.class))).thenReturn(new UserResponse.Payment(new URL(url)));
        //then
        mockMvc.perform(get("/api/subscribe")
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.payment", is(url)))
                .andReturn();
    }

    @Test
    @DisplayName("구독 확인")
    void subscribeOk() throws Exception {
        //given
        String orderCode = "orderCode";
        String accessToken = MyJwtProvider.createAccessToken(user);
        //when then
        mockMvc.perform(get("/api/subscribe/success?order_code="+orderCode)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("구독 취소")
    void subscribeCancel() throws Exception {
        //given
        String accessToken = MyJwtProvider.createAccessToken(user);
        //when then
        mockMvc.perform(delete("/api/subscribe")
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("구독 세션")
    void subscribeSession() throws Exception {
        //given
        String accessToken = MyJwtProvider.createAccessToken(user);
        //when
        when(userService.subscribeSession(any(User.class))).thenReturn(new UserResponse.Session("session"));
        //then
        mockMvc.perform(get("/api/subscribe/session")
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.session", is("session")))
                .andReturn();
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        //given
        Map<String, String> user = new HashMap<>();
        user.put("email","test@example.com");
        user.put("password","123456");
        given(userService.login(any()))
                .willReturn(new HttpHeaders());

        //when
        mockMvc.perform(post("/api/login")
                .with(csrf())
                .contentType(contentType)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        verify(userService).login(any());

    }
}