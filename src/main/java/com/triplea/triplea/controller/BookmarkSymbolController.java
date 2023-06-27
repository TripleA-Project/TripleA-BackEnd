package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.service.BookmarkSymbolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관심 심볼")
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class BookmarkSymbolController {

    private final BookmarkSymbolService bookmarkSymbolService;

    @Operation(summary = "심볼 조회(추천)")
    @GetMapping("/symbol/recommand")
    public ResponseEntity<?> getRecommendedSymbol(){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = bookmarkSymbolService.recommendBookmarkSymbol();

        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }

    @Operation(summary = "심볼 조회(관심)")
    @GetMapping("/auth/symbol/like")
    public ResponseEntity<?> getLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = bookmarkSymbolService.getLikedBookmarkSymbol(myUserDetails.getUser());

        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }

    @Operation(summary = "관심 심볼 생성")
    @GetMapping("/auth/symbol")
    public ResponseEntity<?> saveLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                            @RequestParam("symbol") String symbol) {
        bookmarkSymbolService.saveLikeSymbol(myUserDetails.getUser().getId(), symbol);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @Operation(summary = "관심 심볼 삭제")
    @DeleteMapping("/auth/symbol/{id}")
    public ResponseEntity<?> deleteLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {
        bookmarkSymbolService.deleteLikeSymbol(myUserDetails.getUser(), id);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
