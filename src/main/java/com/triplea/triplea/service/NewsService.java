package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.news.NewsRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.triplea.triplea.dto.news.ApiResponse.*;
import static com.triplea.triplea.dto.news.NewsResponse.*;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    private final BookmarkNewsRepository bookmarkNewsRepository;

    private final int maxDataSize = 1000;

    @Value("${moya.token}")
    private String moyaToken;

    @Transactional(readOnly = true)
    public GNewsDTO searchAllNews(User user, Pageable pageable) {

        int pageSize = pageable.getPageSize();//size
        long pageNumber = pageable.getPageNumber();//number

        if(pageNumber + pageSize > maxDataSize) {
            throw new Exception400("Pageable", "Request exceeds maximum data size(1000).");
        }

        //API 쿼리 파라미터 의미
        //limit: 가져올 뉴스 개수
        //offset: 받은 Data 배열 인덱스 0 부터의 거리. 100이면 100번째 뉴스부터 limit 개수만큼 받는다
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("limit", pageSize)
                .queryParam("offset", pageNumber);

        String url = builder.toUriString();

        ResponseEntity< GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();

            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = datas.stream()
                    .map(data -> {
                        List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findByNewsId(data.getId());
                        Optional<BookmarkNews> opBookmark = bookmarkNewsRepository.findByNewsIdAndUser(data.getId(), user);

                        BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkNewsList.size(), opBookmark.isPresent());

                        return new NewsDTO(data, bookmarkDTO);
                    })
                    .collect(Collectors.toList());

            GNewsDTO gNewsDTO = new GNewsDTO();
            gNewsDTO.setNextPage(globalNewsDTO.getNextPage());
            gNewsDTO.setNews(newsDTOList);

            return gNewsDTO;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }


    @Transactional(readOnly = true)
    public GNewsDTO searchSymbolNews(User user, String symbol, Pageable pageable) {

        int pageSize = pageable.getPageSize();//size
        int pageNumber = pageable.getPageNumber();//page

        if(pageSize > maxDataSize) {
            throw new Exception400("Pageable", "Request exceeds maximum data size(1000).");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("symbol", symbol)
                .queryParam("limit", pageSize);

        if(pageNumber != 0)
            builder.queryParam("nextPage", pageNumber);

        String url = builder.toUriString();

        ResponseEntity< GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();

            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = datas.stream()
                    .map(data -> {
                        List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findByNewsId(data.getId());
                        Optional<BookmarkNews> opBookmark = bookmarkNewsRepository.findByNewsIdAndUser(data.getId(), user);

                        BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkNewsList.size(), opBookmark.isPresent());

                        return new NewsDTO(data, bookmarkDTO);
                    })
                    .collect(Collectors.toList());

            GNewsDTO gNewsDTO = new GNewsDTO();
            gNewsDTO.setNextPage(globalNewsDTO.getNextPage());
            gNewsDTO.setNews(newsDTOList);

            return gNewsDTO;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }
}
