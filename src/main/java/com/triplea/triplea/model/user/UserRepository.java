package com.triplea.triplea.model.user;

import com.triplea.triplea.model.customer.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @NotNull
    @Query("select u from User u where u.id=:id and u.isActive=true")
    Optional<User> findById(@NotNull @Param("id") Long id);
    @Query("select u from User u where u.email=:email and u.isActive=true")
    Optional<User> findUserByEmail(@Param("email") String email);
    @Query("select u from User u where u.email=:email and u.fullName=:name")
    Optional<User> findUserByEmailAndName(@Param("email") String email, @Param("name") String name);

    /**
     * 휴면계정의 이메일도 확인
     */
    @Query("select u from User u where u.email=:email")
    Optional<User> findAllByEmail(@Param("email") String email);

    @Query("select u from User u where u.nextPaymentDate = :nextPaymentDate")
    List<User> findAllByNextPaymentDate(@Param("nextPaymentDate")String nextPaymentDate);

    @Query("select u from User u where u.membership = 0")
    List<User> findAllByUserMembershipBasic();

    @Query("select u from User u where u.membership = 1")
    List<User> findAllByUserMembershipPremium();
}
