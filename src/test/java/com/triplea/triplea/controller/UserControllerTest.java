package com.triplea.triplea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import({MySecurityConfig.class, MyJwtProvider.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
        String accessToken = MyJwtProvider.create(user);
        //when then
        mockMvc.perform(get("/api/subscribe")
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("구독 확인")
    void test() throws Exception {
        //given
        String orderCode = "orderCode";
        String accessToken = MyJwtProvider.create(user);
        //when then
        mockMvc.perform(get("/api/subscribe/success?order_code="+orderCode)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}