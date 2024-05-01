package com.triplea.triplea.model.bookmark;

import com.querydsl.core.annotations.QueryEntity;
import com.triplea.triplea.core.util.timestamp.CreatedTimestamped;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Setter @QueryEntity
@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark_news_tb")
public class BookmarkNews extends CreatedTimestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long newsId;

    @Column(nullable = false)
    private boolean isDeleted;

    @Builder
    public BookmarkNews(Long id, User user, Long newsId, boolean isDeleted) {
        this.id = id;
        this.user = user;
        this.newsId = newsId;
        this.isDeleted = isDeleted;
    }

    public BookmarkNews(User user, Long newsId) {
        this.user = user;
        this.newsId = newsId;
        this.isDeleted = false;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
