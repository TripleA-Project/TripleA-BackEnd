package com.triplea.triplea.model.history;

import com.triplea.triplea.core.util.timestamp.CreatedTimestamped;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history_tb")
public class History extends CreatedTimestamped {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Long newsId;
}
