package com.triplea.triplea.model.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookmarkSymbolRepository extends JpaRepository<BookmarkSymbol, Long> {

    //bookmark_symbol_tb 테이블 symbol_tb 과 조인. symbol_id 중복개수가 많은 내림차순 10개 조회 및 symbol 컬럼 가져오기
    @Query(value = "SELECT s.symbol " +
            "FROM bookmark_symbol_tb b " +
            "INNER JOIN symbol_tb s ON b.symbol_id = s.id " +
            "WHERE b.is_deleted = false and b.user_id IN (SELECT id FROM user_tb WHERE is_active = true)" +
            "GROUP BY s.symbol " +
            "ORDER BY COUNT(b.symbol_id) DESC " +
            "LIMIT 10", nativeQuery = true)
    List<String> findMostFrequentSymbols();
}
