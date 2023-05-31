package com.triplea.triplea.dto;

import com.triplea.triplea.dto.user.UserRequest;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRequestTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @Nested
    @DisplayName("회원가입")
    class Join {
        @Nested
        @DisplayName("이메일")
        class Email {
            @Test
            @DisplayName("실패1: 이메일 형식")
            void test1() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("올바른 형식의 이메일 주소여야 합니다"));
            }

            @Test
            @DisplayName("실패2: 이메일 blank")
            void test2() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email(" ")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> {
                            String errorMsg = error.getMessage();
                            assertThat(errorMsg).isIn("공백일 수 없습니다","올바른 형식의 이메일 주소여야 합니다");
                        });
            }

            @Test
            @DisplayName("실패3: 이메일 empty")
            void test3() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다"));
            }

            @Test
            @DisplayName("실패4: 이메일 null")
            void test4() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email(null)
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다"));
            }
        }
        @Nested
        @DisplayName("비밀번호")
        class Pwd {
            @Test
            @DisplayName("실패1: 비밀번호 형식")
            void test1() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("!@#$")
                        .passwordCheck("!@#$")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("올바른 형식의 비밀번호여야 합니다"));
            }

            @Test
            @DisplayName("실패2: 비밀번호 일치하지 않음")
            void test2() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("12345678")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("password must be equals passwordCheck"));
            }

            @Test
            @DisplayName("실패3: 비밀번호 blank")
            void test3() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password(" ")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> {
                            String errorMsg = error.getMessage();
                            assertThat(errorMsg).isIn("공백일 수 없습니다","올바른 형식의 비밀번호여야 합니다","password must be equals passwordCheck");
                        });
            }

            @Test
            @DisplayName("실패4: 비밀번호 empty")
            void test4() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> {
                            String errorMsg = error.getMessage();
                            assertThat(errorMsg).isIn("공백일 수 없습니다","올바른 형식의 비밀번호여야 합니다","password must be equals passwordCheck");
                        });
            }

            @Test
            @DisplayName("실패5: 비밀번호 null")
            void test5() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password(null)
                        .passwordCheck(null)
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> {
                            String errorMsg = error.getMessage();
                            assertThat(errorMsg).isIn("공백일 수 없습니다","password must be equals passwordCheck");
                        });
            }
        }
        @Nested
        @DisplayName("이름")
        class Name {
            @Test
            @DisplayName("실패1: 이름 blank")
            void test1() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName(" ")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다"));
            }

            @Test
            @DisplayName("실패2: 이름 empty")
            void test2() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("")
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다"));
            }

            @Test
            @DisplayName("실패3: 이름 null")
            void test3() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName(null)
                        .newsLetter(true)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다"));
            }
        }
        @Nested
        @DisplayName("뉴스레터")
        class NewsLetter {
            @Test
            @DisplayName("실패1: 뉴스레터 null")
            void test1() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(null)
                        .emailVerified(true)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("널이어서는 안됩니다"));
            }
        }
        @Nested
        @DisplayName("이메일 인증")
        class EmailVerified {
            @Test
            @DisplayName("실패1: 이메일 인증 null")
            void test1() {
                //given
                UserRequest.Join join = UserRequest.Join.builder()
                        .email("test@example.com")
                        .password("123456")
                        .passwordCheck("123456")
                        .fullName("tester")
                        .newsLetter(true)
                        .emailVerified(null)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
                violations.forEach(
                        error -> assertThat(error.getMessage()).isEqualTo("널이어서는 안됩니다"));
            }
        }
    }
}