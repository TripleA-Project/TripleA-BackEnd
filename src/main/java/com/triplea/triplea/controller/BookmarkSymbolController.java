package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.service.BookmarkSymbolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class BookmarkSymbolController {

    private final BookmarkSymbolService bookmarkSymbolService;

    @GetMapping("/symbol/recommand")
    public ResponseEntity<?> getRecommendedSymbol(){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = bookmarkSymbolService.recommendBookmarkSymbol();

        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }

    @GetMapping("/auth/symbol/like")
    public ResponseEntity<?> getLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = bookmarkSymbolService.getLikedBookmarkSymbol(myUserDetails.getUser());

        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }

    @GetMapping("/auth/symbol")
    public ResponseEntity<?> saveLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                            @RequestParam("symbol") String symbol) {
        bookmarkSymbolService.saveLikeSymbol(myUserDetails.getUser().getId(), symbol);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @DeleteMapping("/auth/symbol/{id}")
    public ResponseEntity<?> deleteLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {
        bookmarkSymbolService.deleteLikeSymbol(myUserDetails.getUser(), id);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
