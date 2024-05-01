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
    @Column(nullable = false)
    private String customerCode;
    private Long subscriptionId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private boolean isActive;
    private String nextPaymentDate;

    @Builder
    public Customer(Long id, String customerCode, User user) {
        this.id = id;
        this.customerCode = customerCode;
        this.user = user;
        this.isActive = false;
    }
    public void addNextPaymentDate(String nextPaymentDate){
        this.nextPaymentDate = nextPaymentDate;
    }

    public void subscribe(Long subscriptionId){
        if(!this.isActive) this.isActive = true;
        this.subscriptionId = subscriptionId;
        this.user.changeMembership(User.Membership.PREMIUM);
    }
    public void deactivateSubscription(){
        this.isActive = false;
        this.user.changeMembership(User.Membership.BASIC);
    }
}
