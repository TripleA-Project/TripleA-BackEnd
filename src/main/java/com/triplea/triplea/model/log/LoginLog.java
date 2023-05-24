package com.triplea.triplea.model.log;

import com.triplea.triplea.core.util.Timestamped;
import com.triplea.triplea.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder @Entity
@Table(name = "login_log_tb")
public class LoginLog extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false)
    private String userAgent;
    @Column(nullable = false)
    private String clientIP;

    public void LastLoginDate(String userAgent, String clientIP){
        this.userAgent = userAgent;
        this.clientIP = clientIP;
    }
}
