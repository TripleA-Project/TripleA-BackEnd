package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MailUtils;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final MailUtils mailUtils;

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
}
