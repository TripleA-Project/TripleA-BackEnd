package com.triplea.triplea.model.bookmark;

import java.time.LocalDate;
import java.util.List;

public interface BookmarkNewsQuerydslRepository {
    List<BookmarkNews> findByCreatedAtAndUser(LocalDate date, Long userId);
}
