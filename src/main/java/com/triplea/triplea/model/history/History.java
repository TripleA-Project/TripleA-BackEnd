package com.triplea.triplea.model.history;

import com.querydsl.core.annotations.QueryEntity;
import com.triplea.triplea.core.util.timestamp.CreatedTimestamped;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@QueryEntity
@Getter
@NoArgsConstructor
@Table(name = "history_tb")
public class History extends CreatedTimestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Long newsId;

    @Builder
    public History(Long id, User user, Long newsId) {
        this.id = id;
        this.user = user;
        this.newsId = newsId;
    }

    @Builder
    public History(User user, Long newsId) {
        this.user = user;
        this.newsId = newsId;
    }
}
