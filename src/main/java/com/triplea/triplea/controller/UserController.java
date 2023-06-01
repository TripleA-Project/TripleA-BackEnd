package com.triplea.triplea.controller;

import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
