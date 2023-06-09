package com.triplea.triplea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.MoyaNewsProvider;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.category.Category;
import com.triplea.triplea.model.category.CategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.user.User;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class NewsServiceTest {
    @InjectMocks
    private NewsService newsService;
    @Mock
    BookmarkNewsRepository bookmarkNewsRepository;
    @Mock
    MainCategoryRepository mainCategoryRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    MoyaNewsProvider newsProvider;

    private final User user = User.builder()
            .id(1L)
            .email("test@example.com")
            .password("123456")
            .fullName("tester")
            .newsLetter(true)
            .emailVerified(true)
            .userAgent("Custom User Agent")
            .clientIP("127.0.0.1")
            .profile("profile1")
            .build();

    @Nested
    @DisplayName("뉴스 조회(키워드)")
    class NewsKeyword {
        @Test
        @DisplayName("성공")
        void test1() throws IOException {
            //given
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            List<ApiResponse.Details> datas = new ArrayList<>();
            String category = "Business & Industrial";
            ApiResponse.Details data = ApiResponse.Details.builder()
                    .id(1L)
                    .symbol("symbol")
                    .source("source")
                    .title("title")
                    .description("desc")
                    .summary("summary")
                    .thumbnail("thumbnail")
                    .url("url")
                    .publishedDate("2019-03-26T14:30:00.000Z")
                    .content("content")
                    .category(category)
                    .keyword1(keyword)
                    .keyword2("k2")
                    .keyword3("k3")
                    .sentiment(1)
                    .build();
            datas.add(data);
            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(datas);
            ResponseBody keywordbody = ResponseBody.create("{\"id\": 1}", MediaType.parse("application/json"));
            Response keywordResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(keywordbody)
                    .build();
            ResponseBody newsbody = ResponseBody.create(json, MediaType.parse("application/json"));
            Response newsResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(newsbody)
                    .build();
            when(newsProvider.getNewsIdByKeyword(keyword))
                    .thenReturn(keywordResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(List.of(1L));
            when(bookmarkNewsRepository.findByNewsIdAndUser(anyLong(),any(User.class)))
                    .thenReturn(Optional.empty());
            when(bookmarkNewsRepository.countBookmarkNewsByNewsId(anyLong()))
                    .thenReturn(0);
            when(newsProvider.getNewsById(anyLong()))
                    .thenReturn(newsResponse);
            when(newsProvider.getNewsDetails(any(Response.class)))
                    .thenReturn(data);

            NewsResponse.News result = newsService.getNewsByKeyword(keyword, size, page, user);
            //then
            verify(newsProvider, times(1)).getNewsIdByKeyword(keyword);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            verify(bookmarkNewsRepository, times(1)).findByNewsIdAndUser(anyLong(), any(User.class));
            verify(bookmarkNewsRepository, times(1)).countBookmarkNewsByNewsId(anyLong());
            verify(newsProvider, times(1)).getNewsById(anyLong());
            verify(newsProvider, times(1)).getNewsDetails(any(Response.class));
            Assertions.assertNull(result.getNextPage());
            Assertions.assertEquals(1, result.getNews().size());
            Assertions.assertEquals(data.getId(), result.getNews().get(0).getNewsId());
            Assertions.assertEquals(data.getTitle(), result.getNews().get(0).getTitle());
            Assertions.assertEquals(data.getDescription(), result.getNews().get(0).getDescription());
            Assertions.assertEquals(data.getSource(), result.getNews().get(0).getSource());
            Assertions.assertEquals(data.getPublishedDate(), result.getNews().get(0).getPublishedDate());
            Assertions.assertEquals(false, result.getNews().get(0).getBookmark().getIsBookmark());
            Assertions.assertEquals(0, result.getNews().get(0).getBookmark().getCount());
            Assertions.assertDoesNotThrow(() -> newsService.getNewsByKeyword(keyword, size, page, user));
        }
        @Test
        @DisplayName("성공2: 비회원")
        void test2() throws IOException {
            //given
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(newsProvider.getNewsIdByKeyword(keyword))
                    .thenReturn(mockResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(Collections.emptyList());
            NewsResponse.News result = newsService.getNewsByKeyword(keyword, size, page, null);
            //then
            verify(newsProvider, times(1)).getNewsIdByKeyword(keyword);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            Assertions.assertNull(result.getNextPage());
            Assertions.assertEquals(Collections.emptyList(), result.getNews());
            Assertions.assertDoesNotThrow(() -> newsService.getNewsByKeyword(keyword, size, page, null));
        }
        @Test
        @DisplayName("실패: response null")
        void test3() throws IOException {
            //given
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .build();
            when(newsProvider.getNewsIdByKeyword(keyword))
                    .thenReturn(mockResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(Collections.emptyList());
            //then
            Assertions.assertThrows(Exception500.class, () -> newsService.getNewsByKeyword(keyword, size, page, user));
        }
    }

    @Nested
    @DisplayName("뉴스 조회(카테고리)")
    class NewsCategory {
        @Test
        @DisplayName("성공")
        void test1() throws IOException {
            //given
            Long categoryId = 1L;
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            String mainCategoryEng = "News";
            String mainCategoryKor = "뉴스";
            String categoryEng = "NewsPolitics";
            MainCategory mainCategory = MainCategory.builder()
                    .id(1L)
                    .mainCategoryEng(mainCategoryEng)
                    .build();
            mainCategory.translateMainCategory(mainCategoryKor);
            Category category = Category.builder()
                    .id(1L)
                    .category(categoryEng)
                    .build();
            category.syncMainCategory(mainCategory);
            when(mainCategoryRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mainCategory));
            when(categoryRepository.findCategoriesByMainCategory(anyLong()))
                    .thenReturn(List.of(category));

            ApiResponse.Details data = ApiResponse.Details.builder()
                    .id(1L)
                    .symbol("symbol")
                    .source("source")
                    .title("title")
                    .description("desc")
                    .summary("summary")
                    .thumbnail("thumbnail")
                    .url("url")
                    .publishedDate("2019-03-26T14:30:00.000Z")
                    .content("content")
                    .category(category.getCategory())
                    .keyword1(keyword)
                    .keyword2("k2")
                    .keyword3("k3")
                    .sentiment(1)
                    .build();
            List<ApiResponse.Details> datas = List.of(data);
            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(datas);
            ResponseBody categorybody = ResponseBody.create("{\"id\": 1}", MediaType.parse("application/json"));
            Response categoryResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(categorybody)
                    .build();
            ResponseBody newsbody = ResponseBody.create(json, MediaType.parse("application/json"));
            Response newsResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(newsbody)
                    .build();
            when(newsProvider.getNewsIdByCategory(anyString()))
                    .thenReturn(categoryResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(List.of(1L));
            when(bookmarkNewsRepository.findByNewsIdAndUser(anyLong(),any(User.class)))
                    .thenReturn(Optional.empty());
            when(bookmarkNewsRepository.countBookmarkNewsByNewsId(anyLong()))
                    .thenReturn(0);
            when(newsProvider.getNewsById(anyLong()))
                    .thenReturn(newsResponse);
            when(newsProvider.getNewsDetails(any(Response.class)))
                    .thenReturn(data);

            NewsResponse.News result = newsService.getNewsByCategory(categoryId, size, page, user);
            //then
            verify(newsProvider, times(1)).getNewsIdByCategory(categoryEng);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            verify(bookmarkNewsRepository, times(1)).findByNewsIdAndUser(anyLong(), any(User.class));
            verify(bookmarkNewsRepository, times(1)).countBookmarkNewsByNewsId(anyLong());
            verify(newsProvider, times(1)).getNewsById(anyLong());
            verify(newsProvider, times(1)).getNewsDetails(any(Response.class));
            Assertions.assertNull(result.getNextPage());
            Assertions.assertEquals(1, result.getNews().size());
            Assertions.assertEquals(data.getId(), result.getNews().get(0).getNewsId());
            Assertions.assertEquals(data.getTitle(), result.getNews().get(0).getTitle());
            Assertions.assertEquals(data.getDescription(), result.getNews().get(0).getDescription());
            Assertions.assertEquals(data.getSource(), result.getNews().get(0).getSource());
            Assertions.assertEquals(data.getPublishedDate(), result.getNews().get(0).getPublishedDate());
            Assertions.assertEquals(false, result.getNews().get(0).getBookmark().getIsBookmark());
            Assertions.assertEquals(0, result.getNews().get(0).getBookmark().getCount());
            Assertions.assertDoesNotThrow(() -> newsService.getNewsByCategory(categoryId, size, page, user));
        }
        @Test
        @DisplayName("성공2: 비회원")
        void test2() throws IOException {
            //given
            Long categoryId = 1L;
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            String mainCategoryEng = "News";
            String mainCategoryKor = "뉴스";
            String categoryEng = "NewsPolitics";
            MainCategory mainCategory = MainCategory.builder()
                    .id(1L)
                    .mainCategoryEng(mainCategoryEng)
                    .build();
            mainCategory.translateMainCategory(mainCategoryKor);
            Category category = Category.builder()
                    .id(1L)
                    .category(categoryEng)
                    .build();
            category.syncMainCategory(mainCategory);
            when(mainCategoryRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mainCategory));
            when(categoryRepository.findCategoriesByMainCategory(anyLong()))
                    .thenReturn(List.of(category));

            ResponseBody body = ResponseBody.create("{}", MediaType.parse("application/json"));
            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .body(body)
                    .build();
            when(newsProvider.getNewsIdByCategory(anyString()))
                    .thenReturn(mockResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(Collections.emptyList());
            NewsResponse.News result = newsService.getNewsByCategory(categoryId, size, page, null);
            //then
            verify(newsProvider, times(1)).getNewsIdByCategory(categoryEng);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            Assertions.assertNull(result.getNextPage());
            Assertions.assertEquals(Collections.emptyList(), result.getNews());
            Assertions.assertDoesNotThrow(() -> newsService.getNewsByCategory(categoryId, size, page, null));
        }
        @Test
        @DisplayName("실패: response null")
        void test3() throws IOException {
            //given
            Long categoryId = 1L;
            String keyword = "solutions";
            Integer size = 10;
            Long page = 0L;
            //when
            String mainCategoryEng = "News";
            String mainCategoryKor = "뉴스";
            String categoryEng = "NewsPolitics";
            MainCategory mainCategory = MainCategory.builder()
                    .id(1L)
                    .mainCategoryEng(mainCategoryEng)
                    .build();
            mainCategory.translateMainCategory(mainCategoryKor);
            Category category = Category.builder()
                    .id(1L)
                    .category(categoryEng)
                    .build();
            category.syncMainCategory(mainCategory);
            when(mainCategoryRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mainCategory));
            when(categoryRepository.findCategoriesByMainCategory(anyLong()))
                    .thenReturn(List.of(category));

            Response mockResponse = new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(new Request.Builder().url("https://example.com").build())
                    .build();
            when(newsProvider.getNewsIdByCategory(anyString()))
                    .thenReturn(mockResponse);
            when(newsProvider.getNewsId(any(Response.class)))
                    .thenReturn(Collections.emptyList());
            //then
            Assertions.assertThrows(Exception500.class, () -> newsService.getNewsByCategory(categoryId, size, page, user));
        }
    }
}