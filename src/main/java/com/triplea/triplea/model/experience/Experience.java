package com.triplea.triplea.model.experience;

import com.triplea.triplea.core.util.timestamp.Timestamped;
import com.triplea.triplea.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @Column(nullable = false)
    private Date endDate;

    public void updateEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Experience(User user, Date endDate) {
        this.user = user;
        this.endDate = endDate;
    }
}
