package com.triplea.triplea.model.experience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    @Query("select e from Experience e where e.user.id = :userId")
    public Experience findByUser(@Param("userId") Long userId);
}
