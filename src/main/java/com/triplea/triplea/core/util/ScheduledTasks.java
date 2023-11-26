package com.triplea.triplea.core.util;


import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cancelSubscribe(){
        String now = String.valueOf(LocalDate.now());
        List<User> userList = userRepository.findAllByNextPaymentDate(now);

        for(User user : userList){
            user.changeMembership(User.Membership.BASIC);
        }
    }

}
