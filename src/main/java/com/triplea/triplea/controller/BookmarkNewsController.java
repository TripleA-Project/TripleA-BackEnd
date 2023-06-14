package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.service.BookmarkNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookmarkNewsController {

    private final BookmarkNewsService bookmarkNewsService;

    @PostMapping("/api/news/{id}")
    public ResponseEntity<?> insert(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.insertBookmark(id,  myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/news/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.deleteBookmark(id, myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }
}
