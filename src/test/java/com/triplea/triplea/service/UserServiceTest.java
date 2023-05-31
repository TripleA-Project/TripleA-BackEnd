package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

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
}