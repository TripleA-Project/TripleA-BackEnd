package com.triplea.triplea.model.category;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    @NotNull
    @Cacheable("categoryCache")
    List<MainCategory> findAll();
    @Query("select mc from MainCategory mc where mc.mainCategoryEng=:category")
    Optional<MainCategory> findMainCategoryByMainCategoryEng(@Param("category") String category);

    @Query("select mc from MainCategory mc join fetch mc.categories c where c.category=:category")
    Optional<MainCategory> findMainCategoryBySubCategory(@Param("category") String category);
}
