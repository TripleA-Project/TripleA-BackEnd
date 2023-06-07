package com.triplea.triplea;

import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TripleAApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripleAApplication.class, args);
    }

}
