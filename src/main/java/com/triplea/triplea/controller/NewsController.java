package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/api/news/latest")
    public ResponseEntity<?> getGlobalNews(@AuthenticationPrincipal MyUserDetails myUserDetails){

        List<NewsResponse.NewsDTO> newsDTOList = newsService.전체뉴스조회(myUserDetails.getUser());

        ResponseDTO<?> responseDTO = new ResponseDTO<>(newsDTOList);

        return ResponseEntity.ok().body(responseDTO);
    }


}
