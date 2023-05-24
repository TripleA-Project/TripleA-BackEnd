package com.triplea.triplea.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.triplea.triplea.model.user.UserRepository;

@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository){
        return args -> {
            userRepository.save(newUser("ssar@email.com", "쌀"));
            userRepository.save(newUser("cos@email.com", "코스"));
        };
    }
}
