package com.triplea.triplea.model.category;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "main_category_tb")
public class MainCategory {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mainCategoryEng;
    private String mainCategoryKor;
    @OneToMany(mappedBy = "mainCategory", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @Builder
    public MainCategory(Long id, String mainCategoryEng) {
        this.id = id;
        this.mainCategoryEng = mainCategoryEng;
    }

    public void translateMainCategory(String kor){
        this.mainCategoryKor = kor;
    }
}
