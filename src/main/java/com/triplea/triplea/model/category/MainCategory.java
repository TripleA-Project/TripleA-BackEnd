package com.triplea.triplea.model.category;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "main_category_tb")
public class MainCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mainCategoryEng;
    private String mainCategoryKor;
    @OneToMany(mappedBy = "mainCategory", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> categories;

    public MainCategory(String mainCategoryEng) {
        this.mainCategoryEng = mainCategoryEng;
    }

    public void translateMainCategory(String kor){
        this.mainCategoryKor = kor;
    }
}
