package com.triplea.triplea.core.util;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class CheckMembership {

    public static User.Membership getMembership(User user, CustomerRepository customerRepository, StepPaySubscriber subscriber) {
        if (user.getMembership() == User.Membership.PREMIUM) {
            Customer customer = getCustomer(user, customerRepository);
//            if (!checkSubscription(customer, subscriber)) customer.deactivateSubscription();
        }
        return user.getMembership();
    }

    private static Customer getCustomer(User user, CustomerRepository customerRepository) {
        return customerRepository.findCustomerByUserId(user.getId()).orElseThrow(
                () -> new Exception400("customer", "잘못된 요청입니다"));
    }

    private static boolean checkSubscription(Customer customer, StepPaySubscriber subscriber) {
        Long subscriptionId = customer.getSubscriptionId();
        if (subscriptionId == null) return false;
        try {
            return subscriber.isSubscribe(subscriptionId);
        } catch (Exception e) {
            throw new Exception500("구독 확인 실패: " + e.getMessage());
        }
    }
}
