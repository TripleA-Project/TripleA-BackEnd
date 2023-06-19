package com.triplea.triplea.model.symbol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {

    Optional<Symbol> findById(Long id);
}
