package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/api/news/latest")
    public ResponseEntity<?> getGlobalNews(@AuthenticationPrincipal MyUserDetails myUserDetails, Pageable pageable){

        NewsResponse.GNewsDTO gNewsDTO = newsService.searchAllNews(myUserDetails.getUser(), pageable);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(gNewsDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/api/news")
    public ResponseEntity<?> getSymbolNews(@RequestParam(value = "symbol") @Valid String symbol,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails,
                                           Pageable pageable){

        NewsResponse.GNewsDTO gNewsDTO = newsService.searchSymbolNews(myUserDetails.getUser(), symbol, pageable);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(gNewsDTO);
        return ResponseEntity.ok().body(responseDTO);

    }



}
