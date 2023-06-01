package com.triplea.triplea.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor
@Table(name = "user_tb")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 60)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private boolean newsLetter;

    @Column(nullable = false)
    private boolean isEmailVerified;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private Membership membership;

    public enum Membership{
        BASIC,PREMIUM
    }

    @Builder
    public User(Long id, String email, String password, String fullName, boolean newsLetter) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.newsLetter = newsLetter;
        this.isEmailVerified = false;
        this.isActive = true;
        this.membership = Membership.BASIC;
    }

    public void update(String password, String fullName){
        this.password = password;
        this.fullName = fullName;
    }

    public void subscribeNewsLetter(boolean newsLetter){
        this.newsLetter = newsLetter;
    }

    public void changeMembership(Membership membership){
        this.membership = membership;
    }

    public void deactivateAccount(){
        this.isActive = false;
    }
}
