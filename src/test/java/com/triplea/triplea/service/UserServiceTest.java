package com.triplea.triplea.service;

import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MailUtils;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    ValueOperations<String, String> valueOperations;
    @Mock
    private MailUtils mailUtils;
    @Mock
    private StepPaySubscriber subscriber;

    @Mock
    private MyJwtProvider myJwtProvider;

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
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
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
            UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify(user.getEmail(), "code");
            //when
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(emailVerify.getCode());
            //then
            Assertions.assertDoesNotThrow(() -> userService.emailVerified(emailVerify));
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("1: 잘못된 이메일")
            void test1() {
                //given
                UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify("wrong@email.com", "code");
                //when
                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
                //then
                Assertions.assertThrows(Exception400.class, () -> userService.emailVerified(emailVerify));
            }

            @Test
            @DisplayName("2: 잘못된 코드")
            void test2() {
                //given
                UserRequest.EmailVerify emailVerify = new UserRequest.EmailVerify(user.getEmail(), "wrong");
                //when
                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(redisTemplate.opsForValue().get(anyString())).thenReturn("code");
                //then
                Assertions.assertThrows(Exception400.class, () -> userService.emailVerified(emailVerify));
            }
        }
    }

    @Nested
    @DisplayName("구독")
    class Subscribe {
        @Test
        @DisplayName("성공 1: customer 생성")
        void test1() throws IOException {
            //given
            String productCode = "product_1";
            String priceCode = "price_1";
            ReflectionTestUtils.setField(userService, "productCode", productCode);
            ReflectionTestUtils.setField(userService, "priceCode", priceCode);
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.empty());
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.postCustomer(any(User.class)))
                    .thenReturn(mockResponse);
            when(subscriber.responseCustomer(any(Response.class), anyString(), anyString()))
                    .thenReturn(UserRequest.Order.builder()
                            .customerId(1L)
                            .customerCode("customerCode")
                            .items(List.of(UserRequest.Order.Item.builder()
                                    .productCode(productCode)
                                    .priceCode(priceCode)
                                    .build()))
                            .build());
            when(subscriber.postOrder(any(UserRequest.Order.class)))
                    .thenReturn(mockResponse);
            when(subscriber.getOrderCode(any(Response.class)))
                    .thenReturn("orderCode");
            when(subscriber.getPaymentLink(anyString()))
                    .thenReturn(mockResponse);
            userService.subscribe(user);
            //then
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).postCustomer(user);
            verify(subscriber, times(1)).responseCustomer(any(Response.class), anyString(), anyString());
            verify(customerRepository, times(1)).save(any(Customer.class));
            verify(subscriber, times(1)).postOrder(any(UserRequest.Order.class));
            verify(subscriber, times(1)).getOrderCode(any(Response.class));
            verify(subscriber, times(1)).getPaymentLink(anyString());
            Assertions.assertDoesNotThrow(() -> userService.subscribe(user));
        }

        @Test
        @DisplayName("성공 2: customer 있음")
        void test2() throws IOException {
            //given
            String productCode = "product_1";
            String priceCode = "price_1";
            ReflectionTestUtils.setField(userService, "productCode", productCode);
            ReflectionTestUtils.setField(userService, "priceCode", priceCode);
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            //when
            when(customerRepository.findCustomerByUserId(1L))
                    .thenReturn(Optional.ofNullable(customer));
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.postOrder(any(UserRequest.Order.class)))
                    .thenReturn(mockResponse);
            when(subscriber.getOrderCode(any(Response.class)))
                    .thenReturn("orderCode");
            when(subscriber.getPaymentLink(anyString()))
                    .thenReturn(mockResponse);
            userService.subscribe(user);
            //then
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(0)).postCustomer(user);
            verify(subscriber, times(0)).responseCustomer(any(Response.class), anyString(), anyString());
            verify(customerRepository, times(0)).save(any(Customer.class));
            verify(subscriber, times(1)).postOrder(any(UserRequest.Order.class));
            verify(subscriber, times(1)).getOrderCode(any(Response.class));
            verify(subscriber, times(1)).getPaymentLink(anyString());
            Assertions.assertDoesNotThrow(() -> userService.subscribe(user));
        }
    }

    @Nested
    @DisplayName("구독 확인")
    class SubscribeOk {
        @Test
        @DisplayName("성공")
        void test1() throws IOException {
            //given
            String orderCode = "orderCode";
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.ofNullable(customer));
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.getOrder(anyString()))
                    .thenReturn(mockResponse);
            when(subscriber.getSubscriptionId(any(Response.class)))
                    .thenReturn(1L);
            userService.subscribeOk(orderCode, user);
            //then
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).getOrder(orderCode);
            verify(subscriber, times(1)).getSubscriptionId(mockResponse);
            Assertions.assertDoesNotThrow(() -> userService.subscribeOk(orderCode, user));
        }

        @Test
        @DisplayName("실패: customer 없음")
        void test2() {
            //given
            String orderCode = "orderCode";
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.empty());
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.subscribeOk(orderCode, user));
        }
    }

    @Nested
    @DisplayName("구독 취소")
    class SubscribeCancel {
        @Test
        @DisplayName("성공")
        void test1() throws IOException {
            //given
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            customer.subscribe(1L);
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.of(customer));
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.cancelSubscription(anyLong()))
                    .thenReturn(mockResponse);
            userService.subscribeCancel(user);
            //then
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).cancelSubscription(anyLong());
            Assertions.assertDoesNotThrow(() -> userService.subscribeCancel(user));
        }

        @Test
        @DisplayName("실패: customer 없음")
        void test2() {
            //given
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.empty());
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.subscribeCancel(user));
        }
    }

    @Nested
    @DisplayName("구독 세션")
    class SubscribeSession {
        @Test
        @DisplayName("성공")
        void test1() throws IOException {
            //given
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            customer.subscribe(1L);
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.of(customer));
            ResponseBody body = ResponseBody.create("session", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.getSession(anyLong())).thenReturn(mockResponse);
            UserResponse.Session result = userService.subscribeSession(user);
            //then
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).getSession(anyLong());
            Assertions.assertEquals("session", result.getSession());
        }

        @Test
        @DisplayName("실패1: customer 없음")
        void test2() {
            //given
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.empty());
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.subscribeSession(user));
        }

        @Test
        @DisplayName("실패2: session key 없음")
        void test3() throws IOException {
            //given
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            customer.subscribe(1L);
            //when
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.of(customer));
            ResponseBody body = ResponseBody.create("", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(subscriber.getSession(anyLong())).thenReturn(mockResponse);
            //then
            Assertions.assertThrows(Exception500.class, () -> userService.subscribeSession(user));
        }
    }

    @Nested
    @DisplayName("개인정보 조회")
    class UserDetail {
        @Test
        @DisplayName("성공")
        void test1() {
            //given

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

            //then
            Assertions.assertDoesNotThrow(() -> userService.userDetail(1L));
        }

        @Test
        @DisplayName("실패")
        void test2() {
            //given

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(Exception400.class, () -> userService.userDetail(2L));
        }
    }

    @Nested
    @DisplayName("개인정보 수정")
    class UserUpdate {
        @Test
        @DisplayName("성공")
        void test1() {

            //given
            UserRequest.Update update = UserRequest.Update.builder()
                    .password("123456")
                    .passwordCheck("123456")
                    .newPassword("12341234")
                    .newPasswordCheck("12341234")
                    .fullName("newName")
                    .newsLetter(false)
                    .build();
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(passwordEncoder.matches(any(), any())).thenReturn(true);
            //then
            Assertions.assertDoesNotThrow(() -> userService.userUpdate(update, 1L));
        }

        @Test
        @DisplayName("실패1 : password 불일치")
        void test2() {
            //given
            UserRequest.Update update = UserRequest.Update.builder()
                    .password("1234567")
                    .passwordCheck("1234567")
                    .newPassword("12341234")
                    .newPasswordCheck("12341234")
                    .fullName("newName")
                    .newsLetter(false)
                    .build();
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(passwordEncoder.matches(any(), any())).thenReturn(false);
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.userUpdate(update, 1L));
        }

        @Test
        @DisplayName("실패2 : newPassword 불일치")
        void test3() {
            //given
            UserRequest.Update update = UserRequest.Update.builder()
                    .password("1234567")
                    .passwordCheck("1234567")
                    .newPassword("12341235")
                    .newPasswordCheck("12341234")
                    .fullName("newName")
                    .newsLetter(false)
                    .build();
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(passwordEncoder.matches(any(), any())).thenReturn(false);
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.userUpdate(update, 2L));
        }
    }

    @Nested
    @DisplayName("네이게이션 프로필")
    class NavigationProfile {
        @Test
        @DisplayName("성공")
        void test1() {
            //given

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

            //then
            Assertions.assertDoesNotThrow(() -> userService.navigation(1L));
        }

        @Test
        @DisplayName("실패")
        void test2() {
            //given

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(Exception400.class, () -> userService.navigation(1L));
        }
    }
}