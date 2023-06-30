package com.triplea.triplea.model.history;

import com.triplea.triplea.model.user.User;

import java.time.LocalDate;
import java.util.List;

public interface HistoryQuerydslRepository {
    List<LocalDate> findDateByCreatedAtAndUser(int year, int month, User user);

    List<History> findByCreatedAtAndUser(LocalDate date, Long userId);

    boolean existsByCreatedAtAndUserAndNewsId(LocalDate date, User user, Long newsId);
}
