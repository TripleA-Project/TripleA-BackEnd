package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.service.RedisService;
import com.triplea.triplea.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "회원")
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final RedisService redisService;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.Join join, Errors errors, HttpServletRequest request) {
        userService.join(join, request.getHeader("User-Agent"), request.getRemoteAddr());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.login login,
                                   Errors errors,
                                   HttpServletRequest request) {

        return ResponseEntity.ok()
                .headers(userService.login(login, request.getHeader("User-Agent"), request.getRemoteAddr()))
                .body(new ResponseDTO<>("로그인 성공"));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletResponse response,
                                    @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails,
                                    HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String msg = userService.logout(response, accessToken, myUserDetails);
        return ResponseEntity.ok()
                .body(new ResponseDTO<>(msg));
    }

    @Operation(summary = "액세스 토큰 요청")
    @PostMapping("/refresh")
    public ResponseEntity<?> recreationAccessToken(@CookieValue(value = "refreshToken") String refreshToken) {
        HttpHeaders header = userService.refreshToken(refreshToken);
        return ResponseEntity.ok()
                .headers(header)
                .body(new ResponseDTO<>("AccessToken 재발급 성공"));
    }

    // 이메일 인증 요청
    @Operation(summary = "이메일 인증 요청")
    @PostMapping("/email")
    public ResponseEntity<?> email(@RequestBody @Valid UserRequest.EmailSend request, Errors errors) {
        userService.email(request);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }


    // 이메일 인증 확인
    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerified(@RequestBody @Valid UserRequest.EmailVerify request, Errors errors) {
        String emailKey = userService.emailVerified(request);
        return ResponseEntity.ok().body(new ResponseDTO<>(emailKey));
    }

    // 구독
    @Operation(summary = "구독")
    @GetMapping("/auth/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("url") String url, @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.Payment payment = userService.subscribe(url, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(payment));
    }

    // 구독 확인
    @Operation(summary = "구독 확인")
    @GetMapping("/auth/subscribe/success")
    public ResponseEntity<?> subscribeOk(@RequestParam("order_code") String orderCode, @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.subscribeOk(orderCode, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 구독 취소
    @Operation(summary = "구독 취소")
    @DeleteMapping("/auth/subscribe")
    public ResponseEntity<?> subscribeCancel(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.subscribeCancel(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 구독내역 조회용 세션키
    @Operation(summary = "구독내역 조회용 세션키")
    @GetMapping("/auth/subscribe/session")
    public ResponseEntity<?> subscribeSession(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.Session session = userService.subscribeSession(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(session));
    }

    // 회원탈퇴
    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/auth/user")
    public ResponseEntity<?> deactivateAccount(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails){
        userService.deactivateAccount(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
      
    // 개인정보 조회
    @Operation(summary = "개인정보 조회")
    @GetMapping("/auth/user")
    public ResponseEntity<?> userDetail(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {
        return ResponseEntity.ok()
                .body(new ResponseDTO<>(userService.userDetail(myUserDetails.getUser().getId())));
    }

    // 개인정보 수정
    @Operation(summary = "개인정보 수정")
    @PostMapping("/auth/user")
    public ResponseEntity<?> userUpdate(@RequestBody @Valid UserRequest.Update update,
                                        Errors errors,
                                        @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {

        userService.userUpdate(update, myUserDetails.getUser().getId());
        return ResponseEntity.ok().body(new ResponseDTO<>("수정 성공"));
    }

    @Operation(summary = "네비게이션 프로필")
    @GetMapping("/auth/user/me")
    public ResponseEntity<?> navigation(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails) {

        return ResponseEntity.ok()
                .body(new ResponseDTO<>(userService.navigation(myUserDetails.getUser().getId())));
    }

    // 새 비밀번호 발급
    @Operation(summary = "비밀번호 찾기(새 비밀번호 발급)")
    @PostMapping("/find/password")
    public ResponseEntity<?> newPassword(@RequestBody @Valid UserRequest.NewPassword request, Errors errors){
        userService.newPassword(request);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
