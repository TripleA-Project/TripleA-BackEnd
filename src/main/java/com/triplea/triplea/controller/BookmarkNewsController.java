package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.service.BookmarkNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "북마크 뉴스")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class BookmarkNewsController {

    private final BookmarkNewsService bookmarkNewsService;

    @Operation(summary = "북마크 뉴스 생성")
    @PostMapping("/news/{id}")
    public ResponseEntity<?> insert(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.insertBookmark(id,  myUserDetails.getUser());

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @Operation(summary = "북마크 뉴스 삭제")
    @DeleteMapping("/news/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails){

        bookmarkNewsService.deleteBookmark(id, myUserDetails.getUser());

        return ResponseEntity.ok(new ResponseDTO<>());
    }
}
