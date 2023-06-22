package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.category.Category;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.symbol.Symbol;
import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmark_symbol_tb")
public class BookmarkSymbol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Long symbolId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Symbol symbol;

    private boolean isDeleted;

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
