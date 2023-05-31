package com.triplea.triplea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.dto.user.UserRequest;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Import(MySecurityConfig.class)
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
}