package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
@Table(name = "bookmark_category_tb")
public class BookmarkCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;
    @Column(nullable = false)
    private boolean isDeleted;

    @Builder
    public BookmarkCategory(User user, MainCategory mainCategory) {
        this.user = user;
        this.mainCategory = mainCategory;
        this.isDeleted = false;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }

    public void unDeleteBookmark(){
        this.isDeleted = false;
    }
}
