package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.service.BookmarkNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class BookmarkNewsController {

    private final BookmarkNewsService bookmarkNewsService;

    @PostMapping("/news/{id}")
    public ResponseEntity<?> insert(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.insertBookmark(id,  myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.deleteBookmark(id, myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }
}
