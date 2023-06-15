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
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MyJwtProvider myJwtProvider;
    private final RedisService redisService;
    private final HttpSession session;
    private final MailUtils mailUtils;
    private final StepPaySubscriber subscriber;
    @Value("${step-pay.product-code}")
    private String productCode;
    @Value("${step-pay.price-code}")
    private String priceCode;

    //로그인
    public HttpHeaders login(Map<String, String> user){
        User userPS = userRepository.findUserByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), userPS.getPassword())) {
            System.out.println("mapUser : "+ user.get("password"));
            System.out.println("userPS : "+userPS.getPassword());
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String accessToken = myJwtProvider.createAccessToken(userPS);
        String refreshToken = myJwtProvider.createRefreshToken(userPS);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(1000 * 60 * 60* 24 * 7)
                .secure(false) // https로 바꾸면 true 변경
                .httpOnly(false) // 나중에 true로 변경
                .build();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", accessToken);
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        redisService.setValues(refreshToken, accessToken);
        return headers;
    }
    //로그아웃
    public User findId(Long id){
        return userRepository.findById(id).get();
    }
    // 회원가입
    @Transactional
    public void join(UserRequest.Join join, String userAgent, String ipAddress) {
        try {
            userRepository.save(join.toEntity(
                    passwordEncoder.encode(join.getPassword()),
                    userAgent,
                    ipAddress,
                    "profile" + new Random().nextInt(4)));
        } catch (Exception e) {
            throw new Exception500("User 생성 실패: " + e.getMessage());
        }
    }

    // 이메일 인증 요청
    public String email(UserRequest.EmailSend request) {
        UUID code = UUID.randomUUID();
        session.setAttribute(request.getEmail(), code);
        String html = "<div>인증코드: " + code + "</div>";
        mailUtils.send(request.getEmail(), "[Triple A] 이메일 인증을 진행해주세요.", html);
        return code.toString();
    }

    // 이메일 인증 확인
    public void emailVerified(UserRequest.EmailVerify request) {
        String code;
        try {
            code = session.getAttribute(request.getEmail()).toString();
        } catch (Exception e) {
            throw new Exception400("email", "이메일이 잘못 되었습니다");
        }
        if (!code.equals(request.getCode())) throw new Exception400("code", "인증 코드가 잘못 되었습니다");
    }

    // 구독
    public UserResponse.Payment subscribe(User user) {
        String orderCode;
        // 이미 구독을 한 적 있으면 새로 고객 생성을 하지 않기 위해
        UserRequest.Order order = customerRepository.findCustomerByUserId(user.getId())
                .map(this::createOrderWithExistingCustomer)
                .orElseGet(() -> createOrderWithNewCustomer(user));
        // 주문 생성
        try (Response getOrder = subscriber.postOrder(order)) {
            orderCode = subscriber.getOrderCode(getOrder);
        } catch (IOException e) {
            throw new Exception500("주문 생성 실패: " + e.getMessage());
        }
        // 결제 링크
        try (Response getLink = subscriber.getPaymentLink(orderCode)) {
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
        Customer customer = getCustomer(user);
        try (Response response = subscriber.getOrder(orderCode)) {
            if (response.isSuccessful()) {
                Long subscriptionId = subscriber.getSubscriptionId(response);
                customer.subscribe(subscriptionId);
                user.changeMembership(User.Membership.PREMIUM);
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
        Customer customer = getCustomer(user);
        try (Response response = subscriber.cancelSubscription(customer.getSubscriptionId())) {
            if (response.isSuccessful()) customer.deactivateSubscription();
        } catch (Exception e) {
            throw new Exception500("구독 취소 실패: " + e.getMessage());
        }
    }

    // 구독내역 조회용 세션키
    public UserResponse.Session subscribeSession(User user) {
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
    private UserRequest.Order createOrderWithNewCustomer(User user) throws Exception500 {
        try (Response postCustomer = subscriber.postCustomer(user)) {
            UserRequest.Order order = subscriber.responseCustomer(postCustomer, productCode, priceCode);
            createCustomer(order, user);
            return order;
        } catch (IOException e) {
            throw new Exception500("고객 생성 실패: " + e.getMessage());
        }
    }

    /**
     * customer 저장
     */
    @Transactional
    public void createCustomer(UserRequest.Order order, User user) {
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
}
