package com.triplea.triplea.model.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkCategoryRepository extends JpaRepository<BookmarkCategory, Long> {
    @Query("select bc from BookmarkCategory bc where bc.user.id=:userId and bc.isDeleted=false")
    List<BookmarkCategory> findBookmarkCategoriesByUser(@Param("userId") Long userId);

    @Query("select bc from BookmarkCategory bc where bc.mainCategory.id = :categoryId and bc.user.id = :userId")
    BookmarkCategory findBookmarkCategoryByMainCategory(@Param("categoryId") Long categoryId, @Param("userId")Long userId);
}
