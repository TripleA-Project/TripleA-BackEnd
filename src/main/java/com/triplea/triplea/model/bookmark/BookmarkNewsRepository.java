package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkNewsRepository extends JpaRepository<BookmarkNews, Long> {

    List<BookmarkNews> findByNewsId(Long newsId);

    Optional<BookmarkNews> findByNewsIdAndUser(Long newsId, User user);

}
