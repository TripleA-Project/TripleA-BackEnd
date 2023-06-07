package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MoyaNewsProvider;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.triplea.triplea.dto.news.ApiResponse.Data;
import static com.triplea.triplea.dto.news.ApiResponse.GlobalNewsDTO;
import static com.triplea.triplea.dto.news.NewsResponse.NewsDTO;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NewsService {

    private final BookmarkNewsRepository bookmarkNewsRepository;

    private final MoyaNewsProvider newsProvider;

    @Value("${moya.token}")
    private String moyaToken;

    @Transactional(readOnly = true)
    public List<NewsDTO> searchAllNews(User user) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.moya.ai/globalnews?token=" + moyaToken;

        ResponseEntity<GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();
            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = new ArrayList<>();
            for (Data data : datas) {

                List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findByNewsId(data.getId());
                Optional<BookmarkNews> opBookmark = bookmarkNewsRepository.findByNewsIdAndUser(data.getId(), user);

                BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkNewsList.size(), opBookmark.isPresent());

                NewsDTO newsDTO = new NewsDTO(data, bookmarkDTO);
                newsDTOList.add(newsDTO);
            }
            return newsDTOList;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

    // 뉴스 조회(키워드)
    public NewsResponse.News getNewsByKeyword(String keyword, int size, Long page, User user) {
        List<NewsResponse.NewsDTO> newsList;
        Long nextPage = null;

        // 키워드로 뉴스 ID 조회
        try (Response keywordResponse = newsProvider.getNewsIdByKeyword(keyword)) {
            List<Long> newsIds = newsProvider.getNewsId(keywordResponse);
            int totalNewsCount = newsIds.size();
            int startIndex = size * (page.intValue());
            int endIndex = Math.min(startIndex + size, totalNewsCount);
            if (endIndex < totalNewsCount) nextPage = page + 1;

            // pagination 구현
            List<Long> newsIdsSubset = newsIds.subList(startIndex, endIndex);
            newsList = newsIdsSubset.stream()
                    .map(newsId -> {
                        // 내가 북마크한 뉴스인지 여부
                        boolean isBookmark = user != null & bookmarkNewsRepository.findByNewsIdAndUser(newsId, user).isPresent();
                        // 총 북마크한 수
                        int bookmarkCount = bookmarkNewsRepository.countBookmarkNewsByNewsId(newsId);

                        // 뉴스 ID로 뉴스 조회
                        try (Response newsResponse = newsProvider.getNewsById(newsId)) {
                            ApiResponse.Details newsDetails = newsProvider.getNewsDetails(newsResponse);
                            return new NewsResponse.NewsDTO(
                                    newsDetails,
                                    BookmarkResponse.BookmarkDTO.builder()
                                            .isBookmark(isBookmark)
                                            .count(bookmarkCount)
                                            .build()
                            );
                        } catch (IOException e) {
                            throw new Exception500("뉴스 조회 실패: " + e.getMessage());
                        }
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception500("키워드 조회 실패: " + e.getMessage());
        }
        return NewsResponse.News.builder()
                .nextPage(nextPage)
                .news(newsList)
                .build();
    }
}
