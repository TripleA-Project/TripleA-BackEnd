package com.triplea.triplea.model.history;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h from History h where h.user=:user")
    Optional<List<History>> findAllByUser(@Param("user")User user);
}
