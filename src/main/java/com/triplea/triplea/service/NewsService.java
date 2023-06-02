package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.bookmark.BookmarkResponse.BookmarkDTO;
import com.triplea.triplea.model.bookmark.Bookmark;
import com.triplea.triplea.model.bookmark.BookmarkRepository;
import com.triplea.triplea.model.news.NewsRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.triplea.triplea.dto.news.ApiResponse.*;
import static com.triplea.triplea.dto.news.NewsResponse.*;

@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    private final BookmarkRepository bookmarkRepository;

    @Value("${moya.token}")
    private String moyaToken;

    @Transactional(readOnly = true)
    public List<NewsDTO> searchAllNews(User user) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.moya.ai/globalnews?token=" + moyaToken;

        ResponseEntity< GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();
            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = new ArrayList<>();
            for (Data data : datas) {

                List<Bookmark> bookmarkList = bookmarkRepository.findByNewsId(data.getId());
                Optional<Bookmark> opBookmark = bookmarkRepository.findByNewsIdAndUser(data.getId(), user);

                BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkList.size(), opBookmark.isPresent());

                NewsDTO newsDTO = new NewsDTO(data, bookmarkDTO);
                newsDTOList.add(newsDTO);
            }
            return newsDTOList;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

}
