package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.core.util.Timestamped;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmakr_tb")
public class Bookmark extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Long newsId;

    private boolean isDeleted;


    public Bookmark(User user, Long newsId) {
        this.user = user;
        this.newsId = newsId;
        this.isDeleted = false;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
