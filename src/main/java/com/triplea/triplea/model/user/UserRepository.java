package com.triplea.triplea.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email=:email and u.isActive=true")
    Optional<User> findUserByEmail(String email);
}
