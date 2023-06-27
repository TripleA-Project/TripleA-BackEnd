package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리")
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @Operation(summary = "카테고리 조회")
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<CategoryResponse> categories = categoryService.getCategories();
        return ResponseEntity.ok().body(new ResponseDTO<>(categories));
    }

    // 카테고리 검색
    @Operation(summary = "카테고리 검색")
    @GetMapping("/category")
    public ResponseEntity<?> searchCategories(@RequestParam("search") String category) {
        List<CategoryResponse> categories = categoryService.searchCategories(category);
        return ResponseEntity.ok().body(new ResponseDTO<>(categories));
    }

    // 관심 카테고리 조회
    @Operation(summary = "관심 카테고리 조회")
    @GetMapping("/auth/category/like")
    public ResponseEntity<?> getLikeCategories(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        List<CategoryResponse> categories = categoryService.getLikeCategories(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @Operation(summary = "관심 카테고리 생성")
    @PostMapping("/auth/category/{id}")
    public ResponseEntity<?> saveLikeCategory(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                              @PathVariable String id) {
        categoryService.saveLikeCategory(myUserDetails.getUser().getId(), Long.valueOf(id));
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @Operation(summary = "관심 카테고리 삭제")
    @DeleteMapping("/auth/category/{id}")
    public ResponseEntity<?> deleteLikeCategory(@PathVariable String id) {
        categoryService.deleteLikeCategory(Long.valueOf(id));
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
