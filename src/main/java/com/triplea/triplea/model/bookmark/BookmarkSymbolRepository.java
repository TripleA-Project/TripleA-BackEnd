package com.triplea.triplea.model.bookmark;

import com.triplea.triplea.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkSymbolRepository extends JpaRepository<BookmarkSymbol, Long> {

    //bookmark_symbol_tb 테이블 symbol_tb 과 조인. symbol_id 중복개수가 많은 내림차순 10개 조회 및 symbol 컬럼 가져오기
    //변경사항: symbol_tb가 삭제되고 조인할 필요 없어짐
    @Query(value = "SELECT b.symbol " +
            "FROM bookmark_symbol_tb b " +
            "WHERE b.is_deleted = false and b.user_id IN (SELECT id FROM user_tb WHERE is_active = true)" +
            "GROUP BY b.symbol " +
            "ORDER BY COUNT(b.symbol) DESC " +
            "LIMIT 10", nativeQuery = true)
    List<String> findMostFrequentSymbols();

    @Query("select bs from BookmarkSymbol bs where bs.id=:id and bs.user=:user and bs.isDeleted=false")
    Optional<BookmarkSymbol> findNonDeletedByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("select bs from BookmarkSymbol bs where bs.symbol=:symbol and bs.user=:user")
    Optional<BookmarkSymbol> findBySymbolAndUser(@Param("symbol") String symbol, @Param("user") User user);

    @Query("select count(bs) from BookmarkSymbol bs where bs.user=:user and bs.isDeleted=false")
    Integer countAllByUser(@Param("user") User user);

    @Query("select bs from BookmarkSymbol bs where bs.user.id=:userId and bs.isDeleted=false")
    List<BookmarkSymbol> findNonDeletedSymbolByUserId(@Param("userId") Long userId);

    @Query("select bs from BookmarkSymbol bs where bs.user=:user")
    Optional<List<BookmarkSymbol>> findAllByUser(@Param("user") User user);
}
