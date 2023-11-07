//package com.triplea.triplea.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.triplea.triplea.core.auth.jwt.BlackListFilter;
//import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
//import com.triplea.triplea.core.config.MySecurityConfig;
//import com.triplea.triplea.core.config.RedisConfig;
//import com.triplea.triplea.core.dummy.DummyEntity;
//import com.triplea.triplea.dto.user.UserRequest;
//import com.triplea.triplea.dto.user.UserResponse;
//import com.triplea.triplea.model.user.User;
//import com.triplea.triplea.service.RedisService;
//import com.triplea.triplea.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Import({MySecurityConfig.class, MyJwtProvider.class, BlackListFilter.class, RedisConfig.class})
//@WebMvcTest(UserController.class)
//class UserControllerTest extends DummyEntity {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private RedisService redisService;
//
//    @MockBean
//    RedisConnectionFactory redisConnectionFactory;
//
//    @BeforeEach
//    public void setUp() {
//        when(redisConnectionFactory.getConnection()).thenReturn(mock(RedisConnection.class));
//    }
//
//    private final MediaType contentType =
//            new MediaType(MediaType.APPLICATION_JSON.getType(),
//                    MediaType.APPLICATION_JSON.getSubtype(),
//                    StandardCharsets.UTF_8);
//
//    private final User user = newMockUser(1L, "test@example.com", "tester");
//
//    @Test
//    @DisplayName("회원가입")
//    void join() throws Exception {
//        //given
//        UserRequest.Join join = UserRequest.Join.builder()
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .passwordCheck(user.getPassword())
//                .fullName(user.getFullName())
//                .newsLetter(true)
//                .emailKey("key")
//                .build();
//        ObjectMapper om = new ObjectMapper();
//        String requestBody = om.writeValueAsString(join);
//
//        //when then
//        mockMvc.perform(post("/api/join")
//                        .with(csrf())
//                        .contentType(contentType)
//                        .content(requestBody)
//                        .header("User-Agent", "Custom User Agent")
//                        .with(request -> {
//                            request.setRemoteAddr("127.0.0.1");
//                            return request;
//                        }))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("이메일 인증 요청")
//    void email() throws Exception {
//        //given
//        UserRequest.EmailSend email = new UserRequest.EmailSend("test@example.com");
//        ObjectMapper om = new ObjectMapper();
//        String requestBody = om.writeValueAsString(email);
//
//        //when then
//        mockMvc.perform(post("/api/email")
//                        .with(csrf())
//                        .contentType(contentType)
//                        .content(requestBody))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("이메일 인증 확인")
//    void emailVerified() throws Exception {
//        //given
//        String key = "key";
//        UserRequest.EmailVerify email = new UserRequest.EmailVerify("test@example.com", "code");
//        ObjectMapper om = new ObjectMapper();
//        String requestBody = om.writeValueAsString(email);
//        given(userService.emailVerified(any())).willReturn(key);
//
//        //when then
//        mockMvc.perform(post("/api/email/verify")
//                        .with(csrf())
//                        .contentType(contentType)
//                        .content(requestBody))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data", is(key)))
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("구독")
//    void subscribe() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//        String url = "https://example.com";
//        when(userService.subscribe(anyString(), any(User.class))).thenReturn(new UserResponse.Payment(new URL(url)));
//        //then
//        mockMvc.perform(get("/api/auth/subscribe?url=" + url)
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.payment", is(url)))
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("구독 확인")
//    void subscribeOk() throws Exception {
//        //given
//        String orderCode = "orderCode";
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when then
//        mockMvc.perform(get("/api/auth/subscribe/success?order_code=" + orderCode)
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("구독 취소")
//    void subscribeCancel() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when then
//        mockMvc.perform(delete("/api/auth/subscribe")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("구독 세션")
//    void subscribeSession() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//        when(userService.subscribeSession(any(User.class))).thenReturn(new UserResponse.Session("session"));
//        //then
//        mockMvc.perform(get("/api/auth/subscribe/session")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.session", is("session")))
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("로그인")
//    void login() throws Exception {
//        //given
//        Map<String, String> user = new HashMap<>();
//        user.put("email", "test@example.com");
//        user.put("password", "123456");
//        given(userService.login(any(), any(), any()))
//                .willReturn(new HttpHeaders());
//
//        //when
//        mockMvc.perform(post("/api/login")
//                        .with(csrf())
//                        .contentType(contentType)
//                        .content(new ObjectMapper().writeValueAsString(user)))
//                .andDo(print())
//                .andExpect(status().isOk());
//        //then
//        verify(userService).login(any(), any(), any());
//
//    }
//
//    @Test
//    @DisplayName("회원탈퇴")
//    void deactivateAccount() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//        //then
//        mockMvc.perform(delete("/api/auth/user")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("개인정보 조회")
//    void userDetail() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        UserResponse.Detail detail = UserResponse.Detail.toDTO(user);
//        //when
//        when(userService.userDetail(any())).thenReturn(detail);
//        //then
//        mockMvc.perform(get("/api/auth/user")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", is("test@example.com")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fullName", is("tester")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.newsLetter", is(true)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.emailVerified", is(true)))
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("개인정보 수정")
//    void userUpdate() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        UserRequest.Update update = UserRequest.Update.builder()
//                .password(user.getPassword())
//                .passwordCheck(user.getPassword())
//                .newPassword(user.getPassword() + "123")
//                .newPasswordCheck(user.getPassword() + "123")
//                .fullName("newName")
//                .newsLetter(false)
//                .build();
//        //when
//        //then
//        mockMvc.perform(post("/api/auth/user")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken)
//                        .contentType(contentType)
//                        .content(new ObjectMapper().writeValueAsString(update)))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
////    @Test
////    @DisplayName("네비게이션 프로필")
////    void navigation() throws Exception {
////        //given
////        String accessToken = MyJwtProvider.createAccessToken(user);
////        UserResponse.Navigation navigation = UserResponse.Navigation.toDTO(user,"nextPaymentDate");
////        //when
////        when(userService.navigation(any())).thenReturn(navigation);
////        //then
////        mockMvc.perform(get("/api/auth/user/me")
////                        .with(csrf())
////                        .header(MyJwtProvider.HEADER, accessToken))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andDo(print())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", is(user.getEmail())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fullName", is(user.getFullName())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.data.membership", is("BASIC")))
////                .andReturn();
////    }
//
//    @Test
//    @DisplayName("새 비밀번호 발급")
//    void newPassword() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        UserRequest.NewPassword request = UserRequest.NewPassword.builder()
//                .email(user.getEmail())
//                .fullName(user.getFullName())
//                .build();
//        ObjectMapper om = new ObjectMapper();
//        String requestBody = om.writeValueAsString(request);
//        //when
//        //then
//        mockMvc.perform(post("/api/find/password")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken)
//                        .contentType(contentType)
//                        .content(requestBody))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//}