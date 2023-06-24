package com.triplea.triplea.service;

import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception401;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MailUtils;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MyJwtProvider myJwtProvider;
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailUtils mailUtils;
    private final StepPaySubscriber subscriber;
    @Value("${step-pay.product-code}")
    private String productCode;
    @Value("${step-pay.price-code}")
    private String priceCode;

    //로그인
    @Transactional
    public HttpHeaders login(UserRequest.login login, String userAgent, String ipAddress) {
        User userPS = userRepository.findUserByEmail(login.getEmail())
                .orElseThrow(() -> new Exception400("Bad-Request", "가입되지 않은 E-MAIL 입니다."));

        passwordCheck(login.getPassword(), userPS.getPassword());
        userPS.lastLoginDate(userAgent, ipAddress);
        String accessToken = myJwtProvider.createAccessToken(userPS);
        String refreshToken = myJwtProvider.createRefreshToken(userPS);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(1000 * 60 * 60 * 24 * 7)
                .secure(false) // https로 바꾸면 true 변경
                .httpOnly(false) // 나중에 true로 변경
                .build();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", accessToken);
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        redisService.setValues(refreshToken, String.valueOf(userPS.getId()));
        return headers;
    }

    // 회원가입
    @Transactional
    public void join(UserRequest.Join join, String userAgent, String ipAddress) {
        duplicateEmail(join.getEmail());
        String key = "code_" + join.getEmail();
        String code = redisTemplate.opsForValue().get(key);
        if (!code.equals(join.getEmailKey())) throw new Exception400("email", "이메일 인증이 되지 않았습니다");
        try {
            userRepository.save(join.toEntity(
                    passwordEncoder.encode(join.getPassword()),
                    true,
                    userAgent,
                    ipAddress,
                    "profile" + new Random().nextInt(4)));
        } catch (Exception e) {
            throw new Exception500("User 생성 실패: " + e.getMessage());
        }
        redisTemplate.delete(key);
    }

    @Transactional
    public String logout(HttpServletResponse response, String accessToken, MyUserDetails myUserDetails) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.replace("Bearer ", ""); // "Bearer " 부분 제거
        }
        String msg = "로그아웃 성공";
        redisService.deleteValues(String.valueOf(myUserDetails.getUser().getId()));
        if (!redisService.existsRefreshToken(String.valueOf(myUserDetails.getUser().getId()))) {
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 쿠키 수명을 0으로 설정하여 즉시 만료
            response.addCookie(cookie);
            redisService.setValuesBlackList(accessToken, "blackList");
            return msg;
        } else throw new Exception401("로그아웃 실패");
    }

    //AccessToken 재발급

    public HttpHeaders refreshToken(String refreshToken) {
        HttpHeaders header = new HttpHeaders();
        if (redisService.existsRefreshToken(refreshToken)) {
            String accessToken = myJwtProvider.recreationAccessToken(refreshToken);
            header.add("Authorization", accessToken);
            return header;
        } else {
            throw new Exception401("RefreshToken 유효하지 않음, 재로그인 요청");
        }
    }

    // 이메일 인증 요청
    public void email(UserRequest.EmailSend request) {
        duplicateEmail(request.getEmail());
        UUID code = UUID.randomUUID();
        String key = "code_" + request.getEmail();
        redisTemplate.opsForValue().set(key, code.toString());
        redisTemplate.expire(key, 3, TimeUnit.MINUTES);
        String html = "<div>인증코드: " + code + "<p style='font-weight:bold;'>해당 인증코드는 3분간 유효합니다.</p></div>";
        mailUtils.send(request.getEmail(), "[Triple A] 이메일 인증을 진행해주세요.", html);
    }

    // 이메일 인증 확인
    public String emailVerified(UserRequest.EmailVerify request) {
        String key = "code_" + request.getEmail();
        String code = redisTemplate.opsForValue().get(key);
        UUID emailVerified = UUID.randomUUID();
        if (code == null) throw new Exception400("code", "인증 코드를 찾을 수 없습니다");
        else if (!code.equals(request.getCode())) throw new Exception400("code", "인증 코드가 잘못 되었습니다");
        else redisTemplate.opsForValue().set(key, emailVerified.toString());
        return emailVerified.toString();
    }

    // 구독
    @Transactional
    public UserResponse.Payment subscribe(String url, User user) {
        User loginUser = getUser(user);
        String orderCode;
        // 이미 구독을 한 적 있으면 새로 고객 생성을 하지 않기 위해
        UserRequest.Order order = customerRepository.findCustomerByUserId(user.getId())
                .map(this::createOrderWithExistingCustomer)
                .orElseGet(() -> createOrderWithNewCustomer(loginUser));
        // 주문 생성
        try (Response getOrder = subscriber.postOrder(order)) {
            orderCode = subscriber.getOrderCode(getOrder);
        } catch (IOException e) {
            throw new Exception500("주문 생성 실패: " + e.getMessage());
        }
        // 결제 링크
        try (Response getLink = subscriber.getPaymentLink(url, orderCode)) {
            if (getLink.isSuccessful()) {
                return new UserResponse.Payment(getLink.request().url().url());
            }
        } catch (IOException e) {
            throw new Exception500("결제링크 생성 실패: " + e.getMessage());
        }
        throw new Exception500("구독 실패");
    }

    // 구독 확인
    @Transactional
    public void subscribeOk(String orderCode, User user) {
        user = getUser(user);
        Customer customer = getCustomer(user);
        try (Response response = subscriber.getOrder(orderCode)) {
            if (response.isSuccessful()) {
                Long subscriptionId = subscriber.getSubscriptionId(response);
                customer.subscribe(subscriptionId);
                return;
            }
        } catch (Exception e) {
            throw new Exception500("주문 상세조회 실패: " + e.getMessage());
        }
        throw new Exception500("subscriptionId 가져오기 실패");
    }

    // 구독 취소
    @Transactional
    public void subscribeCancel(User user) {
        user = getUser(user);
        if(user.getMembership() != User.Membership.PREMIUM) throw new Exception400("subscribe", "구독 중이 아닙니다");
        Customer customer = getCustomer(user);
        cancelSubscription(customer);
    }

    // 구독내역 조회용 세션키
    public UserResponse.Session subscribeSession(User user) {
        user = getUser(user);
        Customer customer = getCustomer(user);
        Long customerId = customer.getId();
        try (Response response = subscriber.getSession(customerId)) {
            if (response.isSuccessful()) {

                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty()) return new UserResponse.Session(json);

                throw new Exception500("Step Pay 세션 생성 API Response 실패");
            }
            throw new Exception500("Step Pay 세션 생성회 API 실패");
        } catch (Exception e) {
            throw new Exception500("세션 생성 실패: " + e.getMessage());
        }
    }

    // 회원탈퇴
    @Transactional
    public void deactivateAccount(User user) {
        user = getUser(user);
        cancelSubscriptionIfSubscribed(user);
        user.deactivateAccount();
    }

    private User getUser(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new Exception401("잘못된 접근입니다"));
    }

    private void cancelSubscriptionIfSubscribed(User user) {
        if (User.Membership.BASIC == user.getMembership()) return;

        Customer customer = getCustomer(user);
        try {
            if (subscriber.isSubscribe(customer.getSubscriptionId())) {
                cancelSubscription(customer);
            } else customer.deactivateSubscription();
        } catch (Exception e) {
            throw new Exception500("구독 확인 실패: " + e.getMessage());
        }
    }

    private void cancelSubscription(Customer customer) {
        try (Response response = subscriber.cancelSubscription(customer.getSubscriptionId())) {
            if (response.isSuccessful()) customer.deactivateSubscription();
        } catch (Exception e) {
            throw new Exception500("구독 취소 실패: " + e.getMessage());
        }
    }

    /**
     * 이미 구독한 적이 있다면 해당 customer 정보로 주문 생성
     */
    private UserRequest.Order createOrderWithExistingCustomer(Customer customer) {
        return UserRequest.Order.builder()
                .customerId(customer.getId())
                .customerCode(customer.getCustomerCode())
                .items(List.of(UserRequest.Order.Item.builder()
                        .productCode(productCode)
                        .priceCode(priceCode)
                        .build()))
                .build();
    }

    /**
     * 구독한 적이 없다면 step pay 고객 생성 API로 고객 등록 후 주문 생성
     */
    private UserRequest.Order createOrderWithNewCustomer(User user) {
        try (Response postCustomer = subscriber.postCustomer(user)) {
            UserRequest.Order order = subscriber.responseCustomer(postCustomer, productCode, priceCode);
            createCustomer(order, user);
            return order;
        } catch (Exception e) {
            throw new Exception500("고객 생성 실패: " + e.getMessage());
        }
    }

    /**
     * customer 저장
     */
    private void createCustomer(UserRequest.Order order, User user) {
        Customer customer = Customer.builder()
                .id(order.getCustomerId())
                .customerCode(order.getCustomerCode())
                .user(user)
                .build();
        try {
            customerRepository.save(customer);
        } catch (Exception e) {
            throw new Exception500("고객 저장 실패: " + e.getMessage());
        }
    }

    /**
     * user id로 customer 찾고 없으면 예외처리
     */
    private Customer getCustomer(User user) {
        return customerRepository.findCustomerByUserId(user.getId()).orElseThrow(
                () -> new Exception400("customer", "잘못된 요청입니다"));
    }

    private User getUser(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("bad-request", "잘못된 요청입니다.")
        );
        return userPS;
    }

    public UserResponse.Detail userDetail(Long userId) {

        return UserResponse.Detail.toDTO(getUser(userId));
    }


    @Transactional
    public void userUpdate(UserRequest.Update update, Long userId) {
        User userPS = getUser(userId);
        passwordCheck(update.getPassword(), userPS.getPassword());
        if (update.getNewPassword() != null) {
            userPS.updatePassword(passwordEncoder.encode(update.getNewPassword()));
        }
        if (update.getFullName() != null) {
            userPS.updateFullName(update.getFullName());
        }
        if (update.getNewsLetter() != null) {
            userPS.updateNewsLetter(update.getNewsLetter());
        }
    }

    public UserResponse.Navigation navigation(Long userId) {

        return UserResponse.Navigation.toDTO(getUser(userId));
    }

    public void passwordCheck(String requestPassword, String persistencePassword) {
        if (!passwordEncoder.matches(requestPassword, persistencePassword)) {
            throw new Exception400("Bad-Request", "잘못된 비밀번호입니다.");
        }
    }

    /**
     * 중복 이메일 검증
     * @param email String
     */
    private void duplicateEmail(String email){
        userRepository.findAllByEmail(email).ifPresent(user -> {throw new Exception400("email", "이미 존재하는 이메일입니다");});
    }
}
