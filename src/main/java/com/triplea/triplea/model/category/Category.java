package com.triplea.triplea.model.category;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor
@Table(name = "category_tb")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String category;
    @ManyToOne(fetch = FetchType.LAZY)
    private MainCategory mainCategory;

    @Builder
    public Category(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public void syncMainCategory(MainCategory mainCategory){
        if(this.mainCategory != null) this.mainCategory.getCategories().remove(this);
        this.mainCategory = mainCategory;
        mainCategory.getCategories().add(this);
    }
}
