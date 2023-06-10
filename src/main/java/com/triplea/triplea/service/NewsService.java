package com.triplea.triplea.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception401;
import com.triplea.triplea.core.exception.Exception404;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MoyaNewsProvider;
import com.triplea.triplea.core.util.Timestamped;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsRequest;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.category.CategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.triplea.triplea.dto.news.ApiResponse.Data;
import static com.triplea.triplea.dto.news.ApiResponse.GlobalNewsDTO;
import static com.triplea.triplea.dto.news.NewsResponse.NewsDTO;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NewsService {

    private final BookmarkNewsRepository bookmarkNewsRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;


    private final int globalNewsMaxSize = 100;

    private final MoyaNewsProvider newsProvider;


    @Value("${moya.token}")
    private String moyaToken;

    @Transactional(readOnly = true)
    public NewsResponse.News searchAllNews(User user, int Size, long page) {

        if(Size > globalNewsMaxSize) {
            throw new Exception400("Size", "Request exceeds maximum data size(1000).");
        }

        //API 쿼리 파라미터 의미
        //limit: 가져올 뉴스 개수
        //offset: 받은 Data 배열 인덱스 0 부터의 거리. 100이면 100번째 뉴스부터 limit 개수만큼 받는다
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("limit", Size);

        if (page != 0)
            builder.queryParam("nextPage", page);

        String url = builder.toUriString();

        ResponseEntity<GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

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

            NewsResponse.News news = NewsResponse.News.builder()
                    .news(newsDTOList)
                    .nextPage(globalNewsDTO.getNextPage())
                    .build();

            return news;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

    @Transactional(readOnly = true)
    public NewsResponse.News searchSymbolNews(User user, String symbol, int size, long page) {

        if (size > globalNewsMaxSize) {
            throw new Exception400("size", "Request exceeds maximum data size(1000).");
        }

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("symbol", symbol)
                .queryParam("limit", size);

        if (page != 0)
            builder.queryParam("nextPage", page);

        String url = builder.toUriString();

        ResponseEntity<GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

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

            NewsResponse.News news = NewsResponse.News.builder()
                    .news(newsDTOList)
                    .nextPage(globalNewsDTO.getNextPage())
                    .build();

            return news;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

    // 뉴스 조회(키워드)
    public NewsResponse.News getNewsByKeyword(String keyword, int size, Long page, User user) {
        List<Long> newsIds;

        // 키워드로 뉴스 ID 조회
        try (Response keywordResponse = newsProvider.getNewsIdByKeyword(keyword)) {
            newsIds = newsProvider.getNewsId(keywordResponse);
        } catch (Exception e) {
            throw new Exception500("키워드 조회 실패: " + e.getMessage());
        }

        NewsRequest.Page pages = getPages(size, page, newsIds);
        List<NewsResponse.NewsDTO> newsList = getNewsByPage(pages, newsIds, user);

        return NewsResponse.News.builder()
                .search(keyword)
                .nextPage(pages.getNextPage())
                .news(newsList)
                .build();

    }

    // 뉴스 조회(카테고리)
    public NewsResponse.News getNewsByCategory(Long id, int size, Long page, User user) {
        List<Long> newsIds = new ArrayList<>();

        // 카테고리 조회
        MainCategory mainCategory = mainCategoryRepository.findById(id).orElseThrow(
                () -> new Exception404("카테고리를 찾을 수 없습니다"));
        categoryRepository.findCategoriesByMainCategory(mainCategory.getId())
                .forEach(category -> {
                    // 대분류 카테고리에 해당하는 모든 카테고리로 API 요청
                    try (Response categoryResponse = newsProvider.getNewsIdByCategory(category.getCategory())) {
                        // 해당 카테고리의 모든 NewsId 값을 가져옴
                        newsIds.addAll(newsProvider.getNewsId(categoryResponse));
                    } catch (Exception e) {
                        throw new Exception500("카테고리 조회 실패: " + e.getMessage());
                    }
                });

        NewsRequest.Page pages = getPages(size, page, newsIds);
        List<NewsResponse.NewsDTO> newsList = getNewsByPage(pages, newsIds, user);

        return NewsResponse.News.builder()
                .search(mainCategory.getMainCategoryKor())
                .nextPage(pages.getNextPage())
                .news(newsList)
                .build();
    }

    /**
     * @param size    페이지별 조회할 뉴스의 갯수
     * @param page    현재 페이지
     * @param newsIds 찾은 모든 NewsId
     * @return NewsRequest.Page
     */
    private NewsRequest.Page getPages(int size, Long page, List<Long> newsIds) {
        int totalNewsCount = newsIds.size();
        int startIndex = size * (page.intValue());
        int endIndex = Math.min(startIndex + size, totalNewsCount);
        Long nextPage = null;
        if (endIndex < totalNewsCount) nextPage = page + 1;
        return NewsRequest.Page.builder()
                .startIndex(startIndex)
                .endIndex(endIndex)
                .nextPage(nextPage)
                .build();
    }

    /**
     * 페이지네이션이 되지 않는 API의 경우 임의로 페이지네이션 구현
     * @param pages   page
     * @param newsIds 찾은 모든 NewsId
     * @param user    북마크를 한 뉴스인지 확인하기 위한 유저 정보
     * @return 뉴스 목록
     */
    private List<NewsResponse.NewsDTO> getNewsByPage(NewsRequest.Page pages, List<Long> newsIds, User user) {
        if (newsIds.isEmpty()) return Collections.emptyList();
        int startIndex = pages.getStartIndex();
        int endIndex = pages.getEndIndex();

        // pagination 구현
        List<Long> newsIdsSubset = newsIds.subList(startIndex, endIndex);
        return newsIdsSubset.stream()
                .map(newsId -> {
                    // 뉴스 ID로 뉴스 조회
                    try (Response newsResponse = newsProvider.getNewsById(newsId)) {
                        ApiResponse.Details newsDetails = newsProvider.getNewsDetails(newsResponse);
                        ApiResponse.MoyaSymbol moyaSymbol = newsProvider.getSymbol(newsDetails.getSymbol(), true);
                        return new NewsResponse.NewsDTO(
                                newsDetails,
                                getLogo(newsDetails.getSymbol(), moyaSymbol.getLogo()),
                                bookmark(newsId, user)
                        );
                    } catch (IOException e) {
                        throw new Exception500("뉴스 조회 실패: " + e.getMessage());
                    }
                }).collect(Collectors.toList());
    }

    /**
     * @param newsId 뉴스 ID
     * @param user   로그인한 유저
     * @return 북마크여부(boolean), 총 북마크 수
     */
    private BookmarkResponse.BookmarkDTO bookmark(Long newsId, User user) {
        // 내가 북마크한 뉴스인지 여부
        boolean isBookmark = user != null & bookmarkNewsRepository.findByNewsIdAndUser(newsId, user).isPresent();
        // 총 북마크한 수
        int bookmarkCount = bookmarkNewsRepository.countBookmarkNewsByNewsId(newsId);

        return BookmarkResponse.BookmarkDTO.builder()
                .isBookmark(isBookmark)
                .count(bookmarkCount)
                .build();
    }

    /**
     * logo 가 없으면 대체하기 위한 메소드
     * @param symbol symbol
     * @param logo logo 검증
     * @return String
     */
    private String getLogo(String symbol, String logo){
        if (logo == null || logo.equals("null")) logo = "https://storage.googleapis.com/iex/api/logos/" + symbol + ".png";
        return logo;
    }
}
