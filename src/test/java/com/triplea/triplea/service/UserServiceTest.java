package com.triplea.triplea.service;

import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception404;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.core.util.mail.MailUtils;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.bookmark.BookmarkCategoryRepository;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.history.HistoryRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest extends DummyEntity {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BookmarkCategoryRepository bookmarkCategoryRepository;
    @Mock
    private BookmarkNewsRepository bookmarkNewsRepository;
    @Mock
    private BookmarkSymbolRepository bookmarkSymbolRepository;
    @Mock
    private HistoryRepository historyRepository;

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

    @Mock
    private RedisService redisService;

    private final User user = newMockUser(1L, "test@example.com", "tester");

    private final Customer customer = newCustomer(user);


    @Nested
    @DisplayName("회원가입")
    class Join {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            String key = "key";
            UserRequest.Join join = UserRequest.Join.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .passwordCheck(user.getPassword())
                    .fullName(user.getFullName())
                    .newsLetter(user.isNewsLetter())
                    .emailKey(key)
                    .build();
            //when
            when(userRepository.findAllByEmail(anyString())).thenReturn(Optional.empty());
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(key);
            //then
            Assertions.assertDoesNotThrow(() -> userService.join(join, user.getUserAgent(), user.getClientIP()));
        }

        @Test
        @DisplayName("실패1: 이미 존재하는 이메일")
        void test2() {
            //given
            String key = "key";
            UserRequest.Join join = UserRequest.Join.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .passwordCheck(user.getPassword())
                    .fullName(user.getFullName())
                    .newsLetter(user.isNewsLetter())
                    .emailKey(key)
                    .build();
            //when
            when(userRepository.findAllByEmail(anyString())).thenReturn(Optional.of(user));
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.join(join, user.getUserAgent(), user.getClientIP()));
        }

        @Test
        @DisplayName("실패2: 인증키 일치하지 않음")
        void test3() {
            //given
            String key = "key";
            UserRequest.Join join = UserRequest.Join.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .passwordCheck(user.getPassword())
                    .fullName(user.getFullName())
                    .newsLetter(user.isNewsLetter())
                    .emailKey(key)
                    .build();
            //when
            when(userRepository.findAllByEmail(anyString())).thenReturn(Optional.empty());
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(redisTemplate.opsForValue().get(anyString())).thenReturn("key2");
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.join(join, user.getUserAgent(), user.getClientIP()));
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
            when(userRepository.findAllByEmail(anyString())).thenReturn(Optional.empty());
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            //then
            Assertions.assertDoesNotThrow(() -> userService.email(emailSend));
        }

        @Test
        @DisplayName("실패: 이미 있는 이메일")
        void test2() {
            //given
            UserRequest.EmailSend emailSend = new UserRequest.EmailSend(user.getEmail());
            //when
            when(userRepository.findAllByEmail(anyString())).thenReturn(Optional.of(user));
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.email(emailSend));
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            when(subscriber.getPaymentLink(anyString(), anyString()))
                    .thenReturn(mockResponse);
            userService.subscribe("http://localhost:3000", user);
            //then
            verify(userRepository, times(1)).findById(anyLong());
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).postCustomer(user);
            verify(subscriber, times(1)).responseCustomer(any(Response.class), anyString(), anyString());
            verify(customerRepository, times(1)).save(any(Customer.class));
            verify(subscriber, times(1)).postOrder(any(UserRequest.Order.class));
            verify(subscriber, times(1)).getOrderCode(any(Response.class));
            verify(subscriber, times(1)).getPaymentLink(anyString(), anyString());
            Assertions.assertDoesNotThrow(() -> userService.subscribe("http://localhost:3000", user));
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            when(subscriber.getPaymentLink(anyString(), anyString()))
                    .thenReturn(mockResponse);
            userService.subscribe("http://localhost:3000/", user);
            //then
            verify(userRepository, times(1)).findById(anyLong());
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(0)).postCustomer(user);
            verify(subscriber, times(0)).responseCustomer(any(Response.class), anyString(), anyString());
            verify(customerRepository, times(0)).save(any(Customer.class));
            verify(subscriber, times(1)).postOrder(any(UserRequest.Order.class));
            verify(subscriber, times(1)).getOrderCode(any(Response.class));
            verify(subscriber, times(1)).getPaymentLink(anyString(), anyString());
            Assertions.assertDoesNotThrow(() -> userService.subscribe("http://localhost:3000/", user));
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            verify(userRepository, times(1)).findById(anyLong());
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            user.changeMembership(User.Membership.PREMIUM);
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            customer.subscribe(1L);
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.of(customer));
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
            //then
            Assertions.assertDoesNotThrow(() -> userService.subscribeCancel(user));
        }

        @Test
        @DisplayName("실패1: customer 없음")
        void test2() {
            //given
            user.changeMembership(User.Membership.PREMIUM);
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(customerRepository.findCustomerByUserId(anyLong()))
                    .thenReturn(Optional.empty());
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.subscribeCancel(user));
        }

        @Test
        @DisplayName("실패2: 구독 중이 아님")
        void test3() {
            //given
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            verify(userRepository, times(1)).findById(anyLong());
            verify(customerRepository, times(1)).findCustomerByUserId(user.getId());
            verify(subscriber, times(1)).getSession(anyLong());
            Assertions.assertEquals("session", result.getSession());
        }

        @Test
        @DisplayName("실패1: customer 없음")
        void test2() {
            //given
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
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
    @DisplayName("회원탈퇴")
    class DeactivateAccount {
//        @Test
//        @DisplayName("성공1: BASIC")
//        void test1() throws IOException {
//            //given
//            //when
//            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//            when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.empty());
//            when(bookmarkCategoryRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
//            when(bookmarkNewsRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
//            when(bookmarkSymbolRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
//            when(historyRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
//            userService.deactivateAccount(user);
//            //then
//            verify(userRepository, times(1)).findById(anyLong());
//            verify(customerRepository, times(1)).findCustomerByUserId(anyLong());
//            verify(subscriber, times(0)).isSubscribe(anyLong());
//            verify(subscriber, times(0)).cancelSubscription(anyLong());
//            verify(bookmarkCategoryRepository, times(0)).deleteAll();
//            verify(bookmarkNewsRepository, times(0)).deleteAll();
//            verify(bookmarkSymbolRepository, times(0)).deleteAll();
//            verify(historyRepository, times(0)).deleteAll();
//            verify(userRepository, times(1)).delete(user);
//            Assertions.assertDoesNotThrow(() -> userService.deactivateAccount(user));
//        }

        @Test
        @DisplayName("성공2: PREMIUM")
        void test2() throws IOException {
            //given
            user.changeMembership(User.Membership.PREMIUM);
            Customer customer = Customer.builder()
                    .id(1L)
                    .user(user)
                    .customerCode("customerCode")
                    .build();
            customer.subscribe(1L);
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response response = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.of(customer));
            when(subscriber.isSubscribe(anyLong())).thenReturn(true);
            when(subscriber.cancelSubscription(anyLong())).thenReturn(response);
            when(bookmarkCategoryRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
            when(bookmarkNewsRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
            when(bookmarkSymbolRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
            when(historyRepository.findAllByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
            userService.deactivateAccount(user);
            //then
            verify(userRepository, times(1)).findById(anyLong());
            verify(customerRepository, times(2)).findCustomerByUserId(anyLong());
            verify(subscriber, times(1)).isSubscribe(anyLong());
            verify(subscriber, times(1)).cancelSubscription(anyLong());
            verify(bookmarkCategoryRepository, times(0)).deleteAll();
            verify(bookmarkNewsRepository, times(0)).deleteAll();
            verify(bookmarkSymbolRepository, times(0)).deleteAll();
            verify(historyRepository, times(0)).deleteAll();
            verify(userRepository, times(1)).delete(user);
            Assertions.assertDoesNotThrow(() -> userService.deactivateAccount(user));
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
//        @Test
//        @DisplayName("성공")
//        void test1() throws IOException {
//            //given
//            //when
//            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
//            Response mockResponse = new Response.Builder()
//                    .code(200)
//                    .message("OK")
//                    .protocol(Protocol.HTTP_1_1)
//                    .body(body)
//                    .request(new Request.Builder().url("https://example.com").build())
//                    .build();
//
//            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
//            when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.ofNullable(customer));
//            when(userService.getCustomerInfo(anyLong())).thenReturn("nextPaymentDate");
//            when(subscriber.getCustomer(anyLong())).thenReturn(mockResponse);
//            //then
//            Assertions.assertDoesNotThrow(() -> userService.navigation(1L));
//        }

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

    @Nested
    @DisplayName("새 비밀번호 발급")
    class NewsPassword {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            UserRequest.NewPassword request = UserRequest.NewPassword.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
            //when
            when(userRepository.findUserByEmailAndName(anyString(), anyString())).thenReturn(Optional.of(user));
            //then
            Assertions.assertDoesNotThrow(() -> userService.newPassword(request));
        }

        @Test
        @DisplayName("실패1: 계정 없음")
        void test2() {
            //given
            UserRequest.NewPassword request = UserRequest.NewPassword.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
            //when
            when(userRepository.findUserByEmailAndName(anyString(), anyString())).thenReturn(Optional.empty());
            //then
            Assertions.assertThrows(Exception404.class, () -> userService.newPassword(request));
        }

        @Test
        @DisplayName("실패2: 탈퇴한 계정")
        void test3() {
            //given
            user.deactivateAccount();
            UserRequest.NewPassword request = UserRequest.NewPassword.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
            //when
            when(userRepository.findUserByEmailAndName(anyString(), anyString())).thenReturn(Optional.of(user));
            //then
            Assertions.assertThrows(Exception400.class, () -> userService.newPassword(request));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            UserRequest.login login = UserRequest.login.builder().email("test@example.com").password("Abcdefg123!@#").build();

            String secretKey = "IyRQvqcFu4I3zWZS"; // 사용하고자하는 비밀 키

            // MyJwtProvider의 SECRET 필드를 설정
            ReflectionTestUtils.setField(myJwtProvider, "SECRET", secretKey);

            //when
            when(userRepository.findUserByEmail(any())).thenReturn(Optional.ofNullable(user));
            when(passwordEncoder.matches(any(), any())).thenReturn(true);

            //then
            Assertions.assertDoesNotThrow(() -> userService.login(login, user.getUserAgent(), user.getClientIP()));
        }

        @Test
        @DisplayName("실패1: 계정없음")
        void test2() {
            //given
            UserRequest.login login = UserRequest.login.builder().email("test@example.com").password("Abcdefg123!@#").build();

            String secretKey = "IyRQvqcFu4I3zWZS"; // 사용하고자하는 비밀 키

            // MyJwtProvider의 SECRET 필드를 설정
            ReflectionTestUtils.setField(myJwtProvider, "SECRET", secretKey);

            //when
            when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(Exception400.class, () -> userService.login(login, user.getUserAgent(), user.getClientIP()));
        }

        @Test
        @DisplayName("실패2: 비밀번호 불일치")
        void test3() {
            //given
            UserRequest.login login = UserRequest.login.builder().email("test@example.com").password("Abcdefg123!@#").build();

            String secretKey = "IyRQvqcFu4I3zWZS"; // 사용하고자하는 비밀 키

            // MyJwtProvider의 SECRET 필드를 설정
            ReflectionTestUtils.setField(myJwtProvider, "SECRET", secretKey);

            //when
            when(userRepository.findUserByEmail(any())).thenReturn(Optional.ofNullable(user));
            when(passwordEncoder.matches(any(), any())).thenReturn(false);

            //then
            Assertions.assertThrows(Exception400.class, () -> userService.login(login, user.getUserAgent(), user.getClientIP()));
        }
    }
}