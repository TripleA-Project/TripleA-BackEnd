package com.triplea.triplea.model.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("select c from Category c where c.category like :keyword%")
    List<Category> findCategoriesByKeyword(@Param("keyword") String keyword);
}
