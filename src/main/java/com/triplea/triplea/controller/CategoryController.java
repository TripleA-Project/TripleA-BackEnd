package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(){
        List<CategoryResponse> categories = categoryService.getCategories();
        return ResponseEntity.ok().body(new ResponseDTO<>(categories));
    }

    // 카테고리 검색
    @GetMapping("/category")
    public ResponseEntity<?> searchCategories(@RequestParam("search") String category){
        List<CategoryResponse> categories = categoryService.searchCategories(category);
        return ResponseEntity.ok().body(new ResponseDTO<>(categories));
    }

    // 관심 카테고리 조회
    @GetMapping("/category/like")
    public ResponseEntity<?> getLikeCategories(@AuthenticationPrincipal MyUserDetails myUserDetails){
        List<CategoryResponse> categories = categoryService.getLikeCategories(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
