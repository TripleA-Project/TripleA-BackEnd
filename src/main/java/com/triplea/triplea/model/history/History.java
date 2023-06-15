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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Long newsId;
}
