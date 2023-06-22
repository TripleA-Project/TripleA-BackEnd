package com.triplea.triplea.model.symbol;

import com.triplea.triplea.model.bookmark.BookmarkCategory;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkSymbol;
import com.triplea.triplea.model.category.Category;
import com.triplea.triplea.model.category.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {

    Optional<Symbol> findById(Long id);
}
