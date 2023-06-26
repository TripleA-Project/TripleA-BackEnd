package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.symbol.Symbol;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmark_symbol_tb")
public class BookmarkSymbol {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long symbolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol_id", nullable = false)
    private Symbol symbol;

    @Column(nullable = false)
    private boolean isDeleted;

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
