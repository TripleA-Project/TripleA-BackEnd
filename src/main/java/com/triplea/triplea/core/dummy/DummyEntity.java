package com.triplea.triplea.core.dummy;

import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DummyEntity {

    public Customer newCustomer(User user){
        return Customer.builder()
                .id(244319L)
                .user(user)
                .customerCode("customer_code")
                .build();
    }

    public User newUser(String email, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode("Abcdefg123!@#"))
                .fullName(fullName)
                .newsLetter(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .clientIP("192.168.1.100")
                .profile("profile1")
                .emailVerified(true)
                .build();
    }

    public User newMockUser(Long id, String email, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .id(id)
                .password(passwordEncoder.encode("Abcdefg123!@#"))
                .fullName(fullName)
                .email(email)
                .newsLetter(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .clientIP("192.168.1.100")
                .profile("profile1")
                .emailVerified(true)
                .build();
        user.changeMembership(User.Membership.PREMIUM);

        return user;
    }
}
