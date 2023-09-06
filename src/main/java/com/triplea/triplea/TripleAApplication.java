package com.triplea.triplea;


import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;


@SpringBootApplication
public class TripleAApplication {

    @Profile("dev")
    @Bean
    CommandLineRunner initDataAdmin(PasswordEncoder passwordEncoder, UserRepository userRepository,
                                    DummyEntity dummyEntity) {

        User mockUser = dummyEntity.newMockUser(1L, "rla7360@gmail.com", "rladnfka12!@");
        return (args)->{
            userRepository.save(mockUser);

        };
    }
    public static void main(String[] args) {
        SpringApplication.run(TripleAApplication.class, args);
    }

}
