package com.triplea.triplea.model.experience;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    private String memo;

    public void updateDate(Date startDate, Date endDate, String memo) {
        this.startDate = startDate;
        this.endDate = endDate;
        if(memo != null && !memo.isEmpty()) this.memo = memo;
    }

    public Experience(User user, Date startDate, Date endDate, String memo) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memo = memo;
    }
}
