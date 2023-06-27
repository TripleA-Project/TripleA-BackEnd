package com.triplea.triplea.model.history;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(value = "select DISTINCT DATE (h.created_at) from history_tb h where year (h.created_at)=:year and month (h.created_at)=:month and h.user_id=:user order by DATE(h.created_at) asc", nativeQuery = true)
    List<Date> findDateTimeByCreatedAtAndUser(@Param("year") int year, @Param("month") int month, @Param("user") User user);

    @Query(value = "select * from history_tb h where DATE(h.created_at)=:date and h.user_id=:userId", nativeQuery = true)
    List<History> findByCreatedAtAndUser(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query(value = "select count(*) AS result from history_tb h where DATE(h.created_at)=:date and h.user_id=:userId and h.news_id=:newsId", nativeQuery = true)
    Long countByCreatedAtAndUserAndNewsId(@Param("date") LocalDate date, @Param("userId") Long userId, @Param("newsId") Long newsId);

    default boolean existsByCreatedAtAndUserAndNewsId(LocalDate date, User user, Long newsId) {
        return countByCreatedAtAndUserAndNewsId(date, user.getId(), newsId) > 0;
    }
}
