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

    @GetMapping("symbol/like")
    public ResponseEntity<?> getLikeSymbol(@AuthenticationPrincipal MyUserDetails myUserDetails){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = bookmarkSymbolService.getLikedBookmarkSymbol(myUserDetails.getUser());

        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }

    @PostMapping("/symbol/{id}")
    public ResponseEntity<?> insertSymbol(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkSymbolService.insertSymbol(id, myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/symbol/{id}")
    public ResponseEntity<?> deleteSymbol(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkSymbolService.deleteSymbol(id, myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }
}
