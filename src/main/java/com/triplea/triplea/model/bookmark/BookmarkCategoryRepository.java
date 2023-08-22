package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkCategoryRepository extends JpaRepository<BookmarkCategory, Long> {
    @Query("select bc from BookmarkCategory bc join fetch bc.mainCategory mc where bc.user.id=:userId and bc.isDeleted=false")
    List<BookmarkCategory> findBookmarkCategoriesByUser(@Param("userId") Long userId);

    @Query("select bc from BookmarkCategory bc where bc.mainCategory.id = :categoryId and bc.user.id = :userId")
    BookmarkCategory findBookmarkCategoryByMainCategory(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Query("select count(bc) from BookmarkCategory bc where bc.user=:user and bc.isDeleted=false")
    Integer countAllByUser(@Param("user") User user);

    @Query("select bc from BookmarkCategory bc where bc.user=:user")
    Optional<List<BookmarkCategory>> findAllByUser(@Param("user") User user);
}
