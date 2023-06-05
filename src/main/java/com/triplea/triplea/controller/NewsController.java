package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@Controller
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news/latest")
    public ResponseEntity<?> getGlobalNews(@AuthenticationPrincipal MyUserDetails myUserDetails){

        List<NewsResponse.NewsDTO> newsDTOList = newsService.searchAllNews(myUserDetails.getUser());

        ResponseDTO<?> responseDTO = new ResponseDTO<>(newsDTOList);

        return ResponseEntity.ok().body(responseDTO);
    }

    // 뉴스 조회(키워드)
    @GetMapping("/news/keyword")
    public ResponseEntity<?> getNewsByKeyword(@RequestParam("keyword") String keyword, @RequestParam("size") int size, @RequestParam("page") Long page, @AuthenticationPrincipal MyUserDetails myUserDetails){
        User user = null;
        if(myUserDetails != null) user = myUserDetails.getUser();
        NewsResponse.News newsList = newsService.getNewsByKeyword(keyword, size, page, user);
        return ResponseEntity.ok().body(new ResponseDTO<>(newsList));
    }
}
