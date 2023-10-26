package com.triplea.triplea.model.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where c.user.id=:id")
    Optional<Customer> findCustomerByUserId(@Param("id") Long id);

    @Query("select c from Customer c where c.nextPaymentDate = :nextPaymentDate")
    List<Customer> findAllByNextPaymentDate(@Param("nextPaymentDate")String nextPaymentDate);
}
