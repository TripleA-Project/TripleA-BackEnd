package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
public class Bookmark {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long contents;

    private boolean isDeleted;

    enum Type{
        SYMBOL, NEWS
    }

    public Bookmark(User user, Type type, Long contents) {
        this.user = user;
        this.type = type;
        this.contents = contents;
        this.isDeleted = false;
    }

    public void deleteBookmark(){
        this.isDeleted = true;
    }
}
