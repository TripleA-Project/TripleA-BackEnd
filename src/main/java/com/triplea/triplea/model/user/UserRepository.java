package com.triplea.triplea.model.user;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @NotNull
    @Query("select u from User u where u.id=:id and u.isActive=true")
    Optional<User> findById(@NotNull @Param("id") Long id);
    @Query("select u from User u where u.email=:email and u.isActive=true")
    Optional<User> findUserByEmail(@Param("email") String emailAddress);
}
