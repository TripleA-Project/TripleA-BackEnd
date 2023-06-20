package com.triplea.triplea.model.history;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h.createdAt from History h where year (h.createdAt)=:year and month (h.createdAt)=:month and h.user=:user order by h.createdAt asc")
    List<ZonedDateTime> findDateTimeByCreatedAtAndUser(@Param("year") int year, @Param("month") int month, @Param("user") User user);

    @Query("select h from History h where function('DATE', h.createdAt)=:date and h.user=:user")
    List<History> findByCreatedAtAndUser(@Param("date") LocalDate date, @Param("user") User user);

    @Query("select count(h) > 0 from History h where function('DATE', h.createdAt)=:date and h.user=:user and h.newsId=:newsId")
    boolean existsByCreatedAtAndUserAndNewsId(@Param("date") LocalDate date, @Param("user") User user, @Param("newsId") Long newsId);
}
