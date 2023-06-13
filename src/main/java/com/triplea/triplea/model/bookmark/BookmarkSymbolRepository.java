package com.triplea.triplea.model.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

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



    @Query("select bn from BookmarkNews bn where bn.isDeleted=false and bn.newsId=:newsId and bn.user.id=:userId and bn.user.isActive=true")
    Optional<BookmarkNews> findNonDeletedByNewsIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    @Query(value = "SELECT s.symbol " +
            "FROM symbol_tb s " +
            "INNER JOIN bookmark_symbol_tb bs " +
            "ON s.id = bs.symbol_id " +
            "WHERE bs.is_deleted = false AND bs.user_id = :userId", nativeQuery = true)
    List<String> findNonDeletedSymbolByUserId(@Param("userId") Long userId);
}
