package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.service.RedisService;
import com.triplea.triplea.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final RedisService redisService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.Join join, Errors errors, HttpServletRequest request) {
        userService.join(join, request.getHeader("User-Agent"), request.getRemoteAddr());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.login login,
                                   HttpServletRequest request) {

        return ResponseEntity.ok()
                .headers(userService.login(login, request.getHeader("User-Agent"), request.getRemoteAddr()))
                .body(new ResponseDTO<>("로그인 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails,
                                    HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String msg = userService.logout(response, accessToken, myUserDetails);
        return ResponseEntity.ok()
                .body(new ResponseDTO<>(msg));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> recreationAccessToken(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                   @CookieValue(value = "refreshToken") String refreshToken) {
        HttpHeaders header = userService.refreshToken(refreshToken, String.valueOf(myUserDetails.getUser().getId()));
        return ResponseEntity.ok()
                .headers(header)
                .body(new ResponseDTO<>("AccessToken 재발급 성공"));
    }

    @GetMapping("loginTest")
    public ResponseEntity<?> loginTest(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(redisService.existsRefreshToken(token));
    }

    // 이메일 인증 요청
    @PostMapping("/email")
    public ResponseEntity<?> email(@RequestBody @Valid UserRequest.EmailSend request, Errors errors) {
        userService.email(request);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }


    // 이메일 인증 확인
    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerified(@RequestBody @Valid UserRequest.EmailVerify request, Errors errors) {
        userService.emailVerified(request);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 구독
    @GetMapping("/subscribe")
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.Payment payment = userService.subscribe(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(payment));
    }

    // 구독 확인
    @GetMapping("/subscribe/success")
    public ResponseEntity<?> subscribeOk(@RequestParam("order_code") String orderCode, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.subscribeOk(orderCode, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 구독 취소
    @DeleteMapping("/subscribe")
    public ResponseEntity<?> subscribeCancel(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.subscribeCancel(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 구독내역 조회용 세션키
    @GetMapping("/subscribe/session")
    public ResponseEntity<?> subscribeSession(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.Session session = userService.subscribeSession(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(session));
    }

    // 개인정보 조회
    @GetMapping("/user")
    public ResponseEntity<?> userDetail(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        return ResponseEntity.ok()
                .body(userService.userDetail(myUserDetails.getUser().getId()));
    }

    // 개인정보 수정
    @PostMapping("/user")
    public ResponseEntity<?> userUpdate(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                        @RequestBody @Valid UserRequest.Update update) {

        userService.userUpdate(update, myUserDetails);
        return ResponseEntity.ok().body(new ResponseDTO<>("수정 성공"));
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> navigation(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        return ResponseEntity.ok()
                .body(userService.navigation(myUserDetails));
    }

}
