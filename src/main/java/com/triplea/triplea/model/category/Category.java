package com.triplea.triplea.model.category;

//import com.triplea.triplea.model.bookmark.BookmarkCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor
@Table(name = "category_tb")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String mainCategory;


    @Builder
    public Category(Long id, String category, String mainCategory) {
        this.id = id;
        this.category = category;
        this.mainCategory = mainCategory;
    }
}
