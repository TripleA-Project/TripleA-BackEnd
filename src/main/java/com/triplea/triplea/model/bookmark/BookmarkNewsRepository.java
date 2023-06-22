package com.triplea.triplea.model.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookmarkNewsRepository extends JpaRepository<BookmarkNews, Long> {
    @Query("select bn from BookmarkNews bn where bn.isDeleted=false and bn.newsId=:newsId and bn.user.isActive=true")
    List<BookmarkNews> findNonDeletedByNewsId(@Param("newsId") Long newsId);

    @Query("select bn from BookmarkNews bn where bn.isDeleted=false and bn.newsId=:newsId and bn.user.id=:userId and bn.user.isActive=true")
    Optional<BookmarkNews> findNonDeletedByNewsIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    // 특정 뉴스(news id)의 활성 상태 유저가 삭제하지 않은 북마크 수
    @Query("select count(bn) from BookmarkNews bn where bn.isDeleted=false and bn.newsId=:newsId and bn.user.isActive=true")
    Integer countBookmarkNewsByNewsId(@Param("newsId") Long newsId);

    @Query(value = "select * from bookmark_news_tb bn where DATE(bn.created_at)=:date and bn.user_id=:userId", nativeQuery = true)
    List<BookmarkNews> findByCreatedAtAndUser(@Param("date")LocalDate date, @Param("userId") Long userId);

}
