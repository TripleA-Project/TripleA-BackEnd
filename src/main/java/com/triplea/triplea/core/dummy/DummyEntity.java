package com.triplea.triplea.core.dummy;

import com.triplea.triplea.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DummyEntity {
    public User newUser(String email, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .newsLetter(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .clientIP("192.168.1.100")
                .profile("profile1")
                .build();
    }

    public User newMockUser(Long id, String email, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(email)
                .newsLetter(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .clientIP("192.168.1.100")
                .profile("profile1")
                .build();
    }
}
