package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MailUtils;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpSession;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private HttpSession session;
    @Mock
    private MailUtils mailUtils;

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

    @Nested
    @DisplayName("회원가입")
    class Join {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            UserRequest.Join join = UserRequest.Join.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .passwordCheck(user.getPassword())
                    .fullName(user.getFullName())
                    .newsLetter(user.isNewsLetter())
                    .emailVerified(user.isEmailVerified())
                    .build();
            //when then
            Assertions.assertDoesNotThrow(() -> userService.join(join, user.getUserAgent(), user.getClientIP()));
        }

        @Test
        @DisplayName("실페: null")
        void test2() {
            //given
            UserRequest.Join join = UserRequest.Join.builder().build();
            //when then
            Assertions.assertThrows(Exception500.class, () -> userService.join(join, user.getUserAgent(), user.getClientIP()));
        }
    }

    @Nested
    @DisplayName("이메일 인증 요청")
    class Email {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            UserRequest.EmailSend emailSend = new UserRequest.EmailSend(user.getEmail());
            //when
            userService.email(emailSend);
            //then
            Assertions.assertDoesNotThrow(() -> userService.email(emailSend));
        }
    }

    @Nested
    @DisplayName("이메일 인증 확인")
    class EmailVerify {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify(user.getEmail(),"code");
            //when
            when(session.getAttribute(anyString()))
                    .thenAnswer(invocation -> {
                        String email = invocation.getArgument(0);
                        if(!email.equals(emailVerify.getEmail())) throw new Exception400("email", "이메일이 잘못 되었습니다");
                        return "code";
                    });
            //then
            Assertions.assertDoesNotThrow(() -> userService.emailVerified(emailVerify));
        }

        @Nested
        @DisplayName("실패")
        class Fail{
            @Test
            @DisplayName("1: 잘못된 이메일")
            void test1(){
                //given
                UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify("wrong@email.com","code");
                //when
                when(session.getAttribute(user.getEmail())).thenReturn("code");
                //then
                Assertions.assertThrows(Exception400.class, () -> userService.emailVerified(emailVerify));
            }
            @Test
            @DisplayName("2: 잘못된 코드")
            void test2(){
                //given
                UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify(user.getEmail(),"wrong");
                //when
                //then
                Assertions.assertThrows(Exception400.class, () -> userService.emailVerified(emailVerify));
            }
        }
    }
}