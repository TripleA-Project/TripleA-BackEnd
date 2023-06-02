package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/api/news/{id}")
    public ResponseEntity<?> insert(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkService.insertBookmark(id,  myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/news/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkService.deleteBookmark(id, myUserDetails.getUser());

        return ResponseEntity.ok().build();
    }
}
