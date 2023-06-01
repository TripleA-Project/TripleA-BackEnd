package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByNewsId(Long newsId);

    Optional<Bookmark> findByNewsIdAndUser(Long newsId, User user);

}
