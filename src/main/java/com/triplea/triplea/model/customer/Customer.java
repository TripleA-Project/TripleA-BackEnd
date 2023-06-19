package com.triplea.triplea.model.customer;

import com.triplea.triplea.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "customer_tb")
public class Customer {
    @Id
    private Long id;
    private String customerCode;
    private Long subscriptionId;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private boolean isActive;

    @Builder
    public Customer(Long id, String customerCode, User user) {
        this.id = id;
        this.customerCode = customerCode;
        this.user = user;
        this.isActive = true;
    }

    public void subscribe(Long subscriptionId){
        if(!this.isActive) this.isActive = true;
        this.subscriptionId = subscriptionId;
    }
    public void deactivateSubscription(){
        this.isActive = false;
        this.user.changeMembership(User.Membership.BASIC);
    }
}
