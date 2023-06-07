package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.core.util.CreatedTimestamped;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmark_news_tb")
public class BookmarkNews extends CreatedTimestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Long newsId;

    private boolean isDeleted;


    public BookmarkNews(User user, Long newsId) {
        this.user = user;
        this.newsId = newsId;
        this.isDeleted = false;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
