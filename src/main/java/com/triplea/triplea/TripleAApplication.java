package com.triplea.triplea;


import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;


@EnableScheduling
@SpringBootApplication
public class TripleAApplication {

//    @Profile("dev")
//    @Bean
//    CommandLineRunner initDataAdmin(PasswordEncoder passwordEncoder, UserRepository userRepository,
//                                    CustomerRepository customerRepository,
//                                    DummyEntity dummyEntity) {
//
//        User mockUser = dummyEntity.newMockUser(0L, "admin@gmail.com", "admin");
//
//        Customer mockCustomer = dummyEntity.newCustomer(mockUser);
//        mockCustomer.addNextPaymentDate("2023-10-23");
//        mockCustomer.subscribe(10748L);
//        return (args)->{
//            userRepository.save(mockUser);
//            customerRepository.save(mockCustomer);
//        };
//    }
    public static void main(String[] args) {
        SpringApplication.run(TripleAApplication.class, args);
    }

}
