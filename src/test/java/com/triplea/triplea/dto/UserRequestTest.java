package com.triplea.triplea.dto;

import com.triplea.triplea.dto.user.UserRequest;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey("key")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
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
                        .emailKey(null)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Join>> violations = validator.validate(join);

                //then
                assertThat(violations).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("이메일 인증")
    class Email {
        @Nested
        @DisplayName("이메일")
        class EmailSend {
            @Test
            @DisplayName("실패1: 이메일 형식")
            void test1() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        "test", "code"
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패2: 이메일 blank")
            void test2() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        " ", "code"
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패3: 이메일 empty")
            void test3() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        "", "code"
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패4: 이메일 null")
            void test4() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        null, "code"
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }
        }

        @Nested
        @DisplayName("코드")
        class Code {
            @Test
            @DisplayName("실패1: 코드 blank")
            void test1() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        "test@example.com", " "
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패2: 코드 empty")
            void test2() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        "test@example.com", ""
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패3: 코드 null")
            void test3() {
                //given
                UserRequest.EmailVerify email = new UserRequest.EmailVerify(
                        "test@example.com", null
                );

                //when
                Set<ConstraintViolation<UserRequest.EmailVerify>> violations = validator.validate(email);

                //then
                assertThat(violations).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("구독")
    class Subscribe {
        @Nested
        @DisplayName("고객")
        class Customer {
            @Nested
            @DisplayName("이름")
            class Name {
                @Test
                @DisplayName("실패1: 이름 blank")
                void test1() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name(" ")
                            .email("test@example.com")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }

                @Test
                @DisplayName("실패2: 이름 empty")
                void test2() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name("")
                            .email("test@example.com")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }

                @Test
                @DisplayName("실패3: 이름 null")
                void test3() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name(null)
                            .email("test@example.com")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }
            }

            @Nested
            @DisplayName("이메일")
            class Email {
                @Test
                @DisplayName("실패1: 이메일 형식")
                void test1() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name("tester")
                            .email("test")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }

                @Test
                @DisplayName("실패2: 이메일 blank")
                void test2() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name("tester")
                            .email(" ")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }

                @Test
                @DisplayName("실패3: 이메일 empty")
                void test3() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name("tester")
                            .email("")
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }

                @Test
                @DisplayName("실패4: 이메일 null")
                void test4() {
                    //given
                    UserRequest.Customer customer = UserRequest.Customer.builder()
                            .name("tester")
                            .email(null)
                            .build();

                    //when
                    Set<ConstraintViolation<UserRequest.Customer>> violations = validator.validate(customer);

                    //then
                    assertThat(violations).isNotNull();
                }
            }
        }

        @Nested
        @DisplayName("주문")
        class Order {
            @Test
            @DisplayName("실패1-1: Items null")
            void test1() {
                //given
                UserRequest.Order order = UserRequest.Order.builder()
                        .items(null)
                        .customerId(1L)
                        .customerCode("customerCode")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Order>> violations = validator.validate(order);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패1-2: Items productCode null")
            void test2() {
                //given
                UserRequest.Order order = UserRequest.Order.builder()
                        .items(List.of(UserRequest.Order.Item.builder()
                                .productCode(null)
                                .priceCode("priceCode")
                                .build()))
                        .customerId(1L)
                        .customerCode("customerCode")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Order>> violations = validator.validate(order);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패1-3: Items priceCode null")
            void test3() {
                //given
                UserRequest.Order order = UserRequest.Order.builder()
                        .items(List.of(UserRequest.Order.Item.builder()
                                .productCode("productCode")
                                .priceCode(null)
                                .build()))
                        .customerId(1L)
                        .customerCode("customerCode")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Order>> violations = validator.validate(order);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패2: customerId null")
            void test4() {
                //given
                UserRequest.Order order = UserRequest.Order.builder()
                        .items(List.of(UserRequest.Order.Item.builder()
                                .productCode("productCode")
                                .priceCode("priceCode")
                                .build()))
                        .customerId(null)
                        .customerCode("customerCode")
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Order>> violations = validator.validate(order);

                //then
                assertThat(violations).isNotNull();
            }

            @Test
            @DisplayName("실패3: customerCode null")
            void test5() {
                //given
                UserRequest.Order order = UserRequest.Order.builder()
                        .items(List.of(UserRequest.Order.Item.builder()
                                .productCode("productCode")
                                .priceCode("priceCode")
                                .build()))
                        .customerId(1L)
                        .customerCode(null)
                        .build();

                //when
                Set<ConstraintViolation<UserRequest.Order>> violations = validator.validate(order);

                //then
                assertThat(violations).isNotNull();
            }
        }
    }
}