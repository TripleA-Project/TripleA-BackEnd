package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark_symbol_tb")
public class BookmarkSymbol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private boolean isDeleted;

    @Builder
    public BookmarkSymbol(Long id, User user, String symbol, boolean isDeleted) {
        this.id = id;
        this.user = user;
        this.symbol = symbol;
        this.isDeleted = isDeleted;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }
    public void bookmark(){this.isDeleted = false;}
}
