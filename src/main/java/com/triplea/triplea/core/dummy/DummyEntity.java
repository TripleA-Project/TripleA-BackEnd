package com.triplea.triplea.core.dummy;

import com.triplea.triplea.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DummyEntity {
    public User newUser(String email, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(email)
                .newsLetter(true)
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
                .build();
    }
}
