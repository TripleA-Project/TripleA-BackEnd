package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.Join join, Errors errors, HttpServletRequest request) {
        userService.join(join, request.getHeader("User-Agent"), request.getRemoteAddr());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    // 이메일 인증 요청
    @PostMapping("/email")
    public ResponseEntity<?> email(@RequestBody @Valid UserRequest.EmailSend request, Errors errors) {
        String code = userService.email(request);
        return ResponseEntity.ok().body(new ResponseDTO<>(code));
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
    public ResponseEntity<?> subscribeSession(@AuthenticationPrincipal MyUserDetails myUserDetails){
        UserResponse.Session session = userService.subscribeSession(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(session));
    }
}
