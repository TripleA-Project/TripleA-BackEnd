package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.news.NewsRequest;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "뉴스")
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "뉴스 조회(최신)")
    @GetMapping("/news/latest")
    public ResponseEntity<?> getGlobalNews(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestParam("size") int size, @RequestParam("page") Long page){

        User user = null;
        if(myUserDetails != null) user = myUserDetails.getUser();
        NewsResponse.News news = newsService.searchAllNews(user, size, page);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(news);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Operation(summary = "뉴스 조회(심볼)")
    @GetMapping("/news")
    public ResponseEntity<?> getSymbolNews(@RequestParam(value = "symbol") @Valid String symbol,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails,
                                           @RequestParam("size") int size, @RequestParam("page") Long page){

        User user = null;
        if(myUserDetails != null) user = myUserDetails.getUser();
        NewsResponse.News news = newsService.searchSymbolNews(user, symbol, size, page);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(news);
        return ResponseEntity.ok().body(responseDTO);

    }

    // 뉴스 조회(키워드)
    @Operation(summary = "뉴스 조회(키워드)")
    @GetMapping("/news/keyword")
    public ResponseEntity<?> getNewsByKeyword(@RequestParam("keyword") String keyword, @RequestParam("size") int size, @RequestParam("page") Long page, @AuthenticationPrincipal MyUserDetails myUserDetails){
        User user = null;
        if(myUserDetails != null) user = myUserDetails.getUser();
        NewsResponse.News newsList = newsService.getNewsByKeyword(keyword, size, page, user);
        return ResponseEntity.ok().body(new ResponseDTO<>(newsList));
    }

    // 뉴스 조회(카테고리)
    @Operation(summary = "뉴스 조회(카테고리)")
    @GetMapping("/news/category/{id}")
    public ResponseEntity<?> getNewsByCategory(@PathVariable Long id, @RequestParam("size") int size, @RequestParam("page") Long page, @AuthenticationPrincipal MyUserDetails myUserDetails){
        User user = null;
        if(myUserDetails != null) user = myUserDetails.getUser();
        NewsResponse.News newsList = newsService.getNewsByCategory(id, size, page, user);
        return ResponseEntity.ok().body(new ResponseDTO<>(newsList));
    }

    // 뉴스 상세 조회
    @Operation(summary = "뉴스 상세 조회")
    @GetMapping("/auth/news/{id}")
    public ResponseEntity<?> getNewsDetails(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        NewsResponse.Details details = newsService.getNewsDetails(id, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(details));
    }

    // 히스토리 조회
    @Operation(summary = "히스토리 조회")
    @GetMapping("/auth/history")
    public ResponseEntity<?> getHistory(@RequestParam("year") int year, @RequestParam("month") int month, @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<NewsResponse.HistoryOut> histories = newsService.getHistory(year, month, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(histories));
    }

    // AI 뉴스 분석
    @Operation(summary = "AI 뉴스 분석")
    @PostMapping("/auth/news/{id}/ai")
    public ResponseEntity<?> getAnalysisAI(@PathVariable Long id, @RequestBody NewsRequest.AI ai, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails){
        NewsResponse.Analysis analysis = newsService.getAnalysisAI(id, ai, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(analysis));
    }
}
