package com.triplea.triplea.model.user;

import com.triplea.triplea.core.util.timestamp.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_tb")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String profile;

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

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private String clientIP;

    public enum Membership {
        BASIC, PREMIUM
    }

    @Builder
    public User(Long id, String email, String password, String fullName, boolean newsLetter, boolean emailVerified, String userAgent, String clientIP, String profile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.newsLetter = newsLetter;
        this.isEmailVerified = emailVerified;
        this.isActive = true;
        this.membership = Membership.BASIC;
        this.userAgent = userAgent;
        this.clientIP = clientIP;
        this.profile = profile;
    }


    @Builder
    public User(String email, String password, String fullName, boolean newsLetter, boolean emailVerified, String userAgent, String clientIP, String profile) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.newsLetter = newsLetter;
        this.isEmailVerified = emailVerified;
        this.isActive = true;
        this.membership = Membership.BASIC;
        this.userAgent = userAgent;
        this.clientIP = clientIP;
        this.profile = profile;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateFullName(String fullName) {
        this.fullName = fullName;
    }

    public void updateNewsLetter(boolean newsLetter) {
        this.newsLetter = newsLetter;
    }

    public void markEmailAsVerified() {
        if (!this.isEmailVerified) this.isEmailVerified = true;
    }

    public void subscribeNewsLetter(boolean newsLetter) {
        this.newsLetter = newsLetter;
    }

    public void changeMembership(Membership membership) {
        this.membership = membership;
    }

    public void deactivateAccount() {
        this.isActive = false;
    }

    public void lastLoginDate(String userAgent, String clientIP) {
        this.userAgent = userAgent;
        this.clientIP = clientIP;
    }
}
