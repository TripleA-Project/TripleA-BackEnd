package com.triplea.triplea.model.enperience;

import com.triplea.triplea.core.util.timestamp.Timestamped;
import com.triplea.triplea.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "experience_tb")
public class Experience extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false, length = 60)
    private String email;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private boolean use_at;
}
