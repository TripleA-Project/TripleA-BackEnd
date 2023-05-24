package com.triplea.triplea.model.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LogRepository extends JpaRepository<LoginLog, Long> {
    @Query("select l from LoginLog l where l.user.id=:id and l.user.isActive=true")
    Optional<LoginLog> findLoginLogByUserId(Long id);
}
