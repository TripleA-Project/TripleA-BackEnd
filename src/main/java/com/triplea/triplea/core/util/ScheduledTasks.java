package com.triplea.triplea.core.util;


import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private final CustomerRepository customerRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cancelSubscribe(){
        String now = String.valueOf(LocalDate.now());
        List<Customer> customerList = customerRepository.findAllByNextPaymentDate(now);

        for(Customer customer : customerList){
            customer.deactivateSubscription();
        }
    }

}
