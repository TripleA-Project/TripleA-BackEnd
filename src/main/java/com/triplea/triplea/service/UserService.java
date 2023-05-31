package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
}
