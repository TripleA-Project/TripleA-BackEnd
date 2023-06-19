package com.triplea.triplea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception401;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.core.util.provide.MoyaNewsProvider;
import com.triplea.triplea.core.util.provide.TiingoStockProvider;
import com.triplea.triplea.core.util.provide.symbol.MoyaSymbolProvider;
import com.triplea.triplea.core.util.provide.symbol.TiingoSymbolProvider;
import com.triplea.triplea.core.util.timestamp.Timestamped;
import com.triplea.triplea.core.util.translate.Papago;
import com.triplea.triplea.core.util.translate.WiseSTGlobal;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.dto.stock.StockRequest;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.category.Category;
import com.triplea.triplea.model.category.CategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.history.History;
import com.triplea.triplea.model.history.HistoryRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
    UserRepository userRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    HistoryRepository historyRepository;
    @Mock
    MoyaNewsProvider newsProvider;
    @Mock
    MoyaSymbolProvider moyaSymbolProvider;
    @Mock
    TiingoSymbolProvider tiingoSymbolProvider;
    @Mock
    TiingoStockProvider stockProvider;
    @Mock
    StepPaySubscriber subscriber;
    @Mock
    Papago papagoTranslator;
    @Mock
    WiseSTGlobal wiseTranslator;
    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    ValueOperations<String, String> valueOperations;

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
            when(bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            when(bookmarkNewsRepository.countBookmarkNewsByNewsId(anyLong()))
                    .thenReturn(0);
            when(newsProvider.getNewsById(anyLong()))
                    .thenReturn(newsResponse);
            when(newsProvider.getNewsDetails(any(Response.class)))
                    .thenReturn(data);
            when(moyaSymbolProvider.getSymbolInfo(anyString())).thenReturn(new SymbolRequest.MoyaSymbol());
            when(tiingoSymbolProvider.getSymbolInfo(anyString())).thenReturn(new SymbolRequest.MoyaSymbol());
            when(moyaSymbolProvider.getLogo(any(SymbolRequest.MoyaSymbol.class))).thenReturn(null);

            NewsResponse.News result = newsService.getNewsByKeyword(keyword, size, page, user);
            //then
            verify(newsProvider, times(1)).getNewsIdByKeyword(keyword);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            verify(bookmarkNewsRepository, times(1)).countBookmarkNewsByNewsId(anyLong());
            verify(newsProvider, times(1)).getNewsById(anyLong());
            verify(newsProvider, times(1)).getNewsDetails(any(Response.class));
            verify(moyaSymbolProvider, times(1)).getSymbolInfo(anyString());
            verify(tiingoSymbolProvider, times(1)).getSymbolInfo(anyString());
            verify(moyaSymbolProvider, times(1)).getLogo(any(SymbolRequest.MoyaSymbol.class));
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
            when(bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            when(bookmarkNewsRepository.countBookmarkNewsByNewsId(anyLong()))
                    .thenReturn(0);
            when(newsProvider.getNewsById(anyLong()))
                    .thenReturn(newsResponse);
            when(newsProvider.getNewsDetails(any(Response.class)))
                    .thenReturn(data);
            when(moyaSymbolProvider.getSymbolInfo(anyString())).thenReturn(new SymbolRequest.MoyaSymbol());
            when(tiingoSymbolProvider.getSymbolInfo(anyString())).thenReturn(new SymbolRequest.MoyaSymbol());
            when(moyaSymbolProvider.getLogo(any(SymbolRequest.MoyaSymbol.class))).thenReturn(null);

            NewsResponse.News result = newsService.getNewsByCategory(categoryId, size, page, user);
            //then
            verify(newsProvider, times(1)).getNewsIdByCategory(categoryEng);
            verify(newsProvider, times(1)).getNewsId(any(Response.class));
            verify(bookmarkNewsRepository, times(1)).findNonDeletedByNewsIdAndUserId(anyLong(), anyLong());
            verify(bookmarkNewsRepository, times(1)).countBookmarkNewsByNewsId(anyLong());
            verify(newsProvider, times(1)).getNewsById(anyLong());
            verify(newsProvider, times(1)).getNewsDetails(any(Response.class));
            verify(moyaSymbolProvider, times(1)).getSymbolInfo(anyString());
            verify(tiingoSymbolProvider, times(1)).getSymbolInfo(anyString());
            verify(moyaSymbolProvider, times(1)).getLogo(any(SymbolRequest.MoyaSymbol.class));
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

    @Nested
    @DisplayName("뉴스 상세 조회")
    class NewsDetails {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("1: PREMIUM")
            void test1() throws IOException {
                //given
                user.changeMembership(User.Membership.PREMIUM);
                Long id = 1L;
                //when
                String mainCategoryEng = "News";
                String mainCategoryKor = "뉴스";
                String categoryEng = "NewsPolitics";
                MainCategory mainCategory = MainCategory.builder()
                        .id(1L)
                        .mainCategoryEng(mainCategoryEng)
                        .build();
                mainCategory.translateMainCategory(mainCategoryKor);
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
                        .category(categoryEng)
                        .keyword1("k1")
                        .keyword2("k2")
                        .keyword3("k3")
                        .sentiment(1)
                        .build();
                List<ApiResponse.Details> datas = List.of(data);
                ObjectMapper om = new ObjectMapper();
                String json = om.writeValueAsString(datas);
                ResponseBody newsbody = ResponseBody.create(json, MediaType.parse("application/json"));
                Response newsResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("https://example.com").build())
                        .body(newsbody)
                        .build();
                SymbolRequest.MoyaSymbol moyaSymbol = SymbolRequest.MoyaSymbol.builder().build();
                SymbolRequest.MoyaSymbol tiingoSymbol = SymbolRequest.MoyaSymbol.builder()
                        .symbol(data.getSymbol())
                        .companyName("company")
                        .build();
                StockRequest.TiingoStock today = StockRequest.TiingoStock.builder().build();
                StockRequest.TiingoStock yesterday = StockRequest.TiingoStock.builder().build();
                StockResponse.Price price = StockResponse.Price.builder()
                        .today(today)
                        .yesterday(yesterday)
                        .build();
                Customer customer = Customer.builder()
                        .id(1L)
                        .user(user)
                        .customerCode("customerCode")
                        .build();
                customer.subscribe(1L);
                NewsResponse.TranslateOut translate = NewsResponse.TranslateOut.builder()
                        .title("제목")
                        .description("설명")
                        .summary("요약")
                        .content("내용")
                        .build();
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                when(historyRepository.existsByCreatedAtAndUserAndNewsId(any(), any(User.class), anyLong())).thenReturn(true);
                when(newsProvider.getNewsById(anyLong())).thenReturn(newsResponse);
                when(newsProvider.getNewsDetails(any(Response.class))).thenReturn(data);
                when(mainCategoryRepository.findMainCategoryBySubCategory(anyString())).thenReturn(Optional.of(mainCategory));
                when(moyaSymbolProvider.getSymbolInfo(anyString())).thenReturn(moyaSymbol);
                when(tiingoSymbolProvider.getSymbolInfo(anyString())).thenReturn(tiingoSymbol);
                when(moyaSymbolProvider.getLogo(any(SymbolRequest.MoyaSymbol.class))).thenReturn("logo");
                when(stockProvider.getStocks(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(price);
                when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.of(customer));
                when(subscriber.isSubscribe(anyLong())).thenReturn(true);
                when(wiseTranslator.translateArticle(any(ApiResponse.Details.class))).thenReturn(translate);
                NewsResponse.Details result = newsService.getNewsDetails(id, user);
                //then
                verify(userRepository, times(1)).findById(anyLong());
                verify(historyRepository, times(1)).existsByCreatedAtAndUserAndNewsId(any(), any(User.class), anyLong());
                verify(newsProvider, times(1)).getNewsById(anyLong());
                verify(newsProvider, times(1)).getNewsDetails(any());
                verify(mainCategoryRepository, times(1)).findMainCategoryBySubCategory(anyString());
                verify(moyaSymbolProvider, times(1)).getSymbolInfo(anyString());
                verify(tiingoSymbolProvider, times(1)).getSymbolInfo(anyString());
                verify(moyaSymbolProvider, times(1)).getLogo(any());
                verify(stockProvider, times(1)).getStocks(anyString(), any(), any());
                verify(customerRepository, times(1)).findCustomerByUserId(anyLong());
                verify(subscriber, times(1)).isSubscribe(anyLong());
                verify(wiseTranslator, times(1)).translateArticle(any());
                Assertions.assertEquals(id, result.getNewsId());
                Assertions.assertEquals(user.getMembership(), result.getUser().getMembership());
                Assertions.assertNull(result.getUser().getHistoryNewsIds());
                Assertions.assertDoesNotThrow(() -> newsService.getNewsDetails(id, user));
            }

            @Test
            @DisplayName("2: BASIC")
            void test2() throws IOException {
                //given
                Long id = 1L;
                //when
                String mainCategoryEng = "News";
                String mainCategoryKor = "뉴스";
                String categoryEng = "NewsPolitics";
                MainCategory mainCategory = MainCategory.builder()
                        .id(1L)
                        .mainCategoryEng(mainCategoryEng)
                        .build();
                mainCategory.translateMainCategory(mainCategoryKor);
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
                        .category(categoryEng)
                        .keyword1("k1")
                        .keyword2("k2")
                        .keyword3("k3")
                        .sentiment(1)
                        .build();
                List<ApiResponse.Details> datas = List.of(data);
                ObjectMapper om = new ObjectMapper();
                String json = om.writeValueAsString(datas);
                ResponseBody newsbody = ResponseBody.create(json, MediaType.parse("application/json"));
                Response newsResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("https://example.com").build())
                        .body(newsbody)
                        .build();
                SymbolRequest.MoyaSymbol moyaSymbol = SymbolRequest.MoyaSymbol.builder().build();
                SymbolRequest.MoyaSymbol tiingoSymbol = SymbolRequest.MoyaSymbol.builder()
                        .symbol(data.getSymbol())
                        .companyName("company")
                        .build();
                StockRequest.TiingoStock today = StockRequest.TiingoStock.builder().build();
                StockRequest.TiingoStock yesterday = StockRequest.TiingoStock.builder().build();
                StockResponse.Price price = StockResponse.Price.builder()
                        .today(today)
                        .yesterday(yesterday)
                        .build();
                String title = "제목";
                List<Long> newsId = List.of(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                when(historyRepository.existsByCreatedAtAndUserAndNewsId(any(), any(User.class), anyLong())).thenReturn(true);
                when(newsProvider.getNewsById(anyLong())).thenReturn(newsResponse);
                when(newsProvider.getNewsDetails(any(Response.class))).thenReturn(data);
                when(mainCategoryRepository.findMainCategoryBySubCategory(anyString())).thenReturn(Optional.of(mainCategory));
                when(moyaSymbolProvider.getSymbolInfo(anyString())).thenReturn(moyaSymbol);
                when(tiingoSymbolProvider.getSymbolInfo(anyString())).thenReturn(tiingoSymbol);
                when(moyaSymbolProvider.getLogo(any(SymbolRequest.MoyaSymbol.class))).thenReturn("logo");
                when(stockProvider.getStocks(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(price);
                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(redisTemplate.opsForValue().get(anyString())).thenReturn(StringUtils.collectionToCommaDelimitedString(newsId));
                when(papagoTranslator.translate(anyString())).thenReturn(title);
                NewsResponse.Details result = newsService.getNewsDetails(id, user);
                //then
                verify(userRepository, times(1)).findById(anyLong());
                verify(historyRepository, times(1)).existsByCreatedAtAndUserAndNewsId(any(), any(User.class), anyLong());
                verify(newsProvider, times(1)).getNewsById(anyLong());
                verify(newsProvider, times(1)).getNewsDetails(any());
                verify(mainCategoryRepository, times(1)).findMainCategoryBySubCategory(anyString());
                verify(moyaSymbolProvider, times(1)).getSymbolInfo(anyString());
                verify(tiingoSymbolProvider, times(1)).getSymbolInfo(anyString());
                verify(moyaSymbolProvider, times(1)).getLogo(any());
                verify(stockProvider, times(1)).getStocks(anyString(), any(), any());
                verify(customerRepository, times(0)).findCustomerByUserId(anyLong());
                verify(subscriber, times(0)).isSubscribe(anyLong());
                verify(wiseTranslator, times(0)).translateArticle(any());
                verify(papagoTranslator, times(1)).translate(anyString());
                Assertions.assertEquals(id, result.getNewsId());
                Assertions.assertEquals(user.getMembership(), result.getUser().getMembership());
                Assertions.assertEquals(10, result.getUser().getHistoryNewsIds().size());
                Assertions.assertDoesNotThrow(() -> newsService.getNewsDetails(id, user));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("1: 비활성화 회원")
            void test1() {
                //given
                Long id = 1L;
                //when
                when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
                //then
                Assertions.assertThrows(Exception401.class, () -> newsService.getNewsDetails(id, user));
            }

            @Test
            @DisplayName("2: 뉴스를 찾을 수 없음")
            void test2() throws IOException {
                //given
                Long id = 1L;
                //when
                ResponseBody newsbody = ResponseBody.create("{}", MediaType.parse("application/json"));
                Response newsResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("https://example.com").build())
                        .body(newsbody)
                        .build();
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                when(newsProvider.getNewsById(anyLong())).thenReturn(newsResponse);
                when(newsProvider.getNewsDetails(any(Response.class))).thenThrow(new Exception400("newsId", "뉴스를 찾을 수 없습니다"));
                //then
                Assertions.assertThrows(Exception500.class, () -> newsService.getNewsDetails(id, user));
            }

            @Test
            @DisplayName("3: MoYa API 실패")
            void test3() throws IOException {
                //given
                Long id = 1L;
                //when
                ResponseBody newsbody = ResponseBody.create("{}", MediaType.parse("application/json"));
                Response newsResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("https://example.com").build())
                        .body(newsbody)
                        .build();
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                when(newsProvider.getNewsById(anyLong())).thenReturn(newsResponse);
                when(newsProvider.getNewsDetails(any(Response.class))).thenThrow(new Exception500("MoYa 뉴스 상세 조회 API 실패"));
                //then
                Assertions.assertThrows(Exception500.class, () -> newsService.getNewsDetails(id, user));
            }

            @Test
            @DisplayName("4: Request 실패(body null)")
            void test4() throws IOException {
                //given
                Long id = 1L;
                //when
                Response newsResponse = new Response.Builder()
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("https://example.com").build())
                        .body(null)
                        .build();
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                when(newsProvider.getNewsById(anyLong())).thenReturn(newsResponse);
                //then
                Assertions.assertThrows(Exception500.class, () -> newsService.getNewsDetails(id, user));
            }
        }
    }

    @Nested
    @DisplayName("히스토리 조회")
    class NewsHistory{
        @Test
        @DisplayName("성공")
        void test(){
            //given
            Long newsId = 1L;
            int year = 2023;
            int month = 6;
            //when
            ZonedDateTime zonedDateTime = ZonedDateTime.now(Timestamped.SEOUL_ZONE_ID);
            List<ZonedDateTime> dateTimes = List.of(zonedDateTime);
            History history = History.builder()
                    .id(1L)
                    .user(user)
                    .newsId(newsId)
                    .build();
            List<History> histories = List.of(history);
            when(historyRepository.findDateTimeByCreatedAtAndUser(anyInt(), anyInt(), any(User.class))).thenReturn(dateTimes);
            when(bookmarkNewsRepository.findByCreatedAtAndUser(any(), any(User.class))).thenReturn(Collections.emptyList());
            when(historyRepository.findByCreatedAtAndUser(any(), any(User.class))).thenReturn(histories);
            List<NewsResponse.HistoryOut> result = newsService.getHistory(year, month, user);
            //then
            verify(historyRepository, times(1)).findDateTimeByCreatedAtAndUser(anyInt(), anyInt(), any(User.class));
            verify(bookmarkNewsRepository, times(1)).findByCreatedAtAndUser(any(), any(User.class));
            verify(historyRepository, times(1)).findByCreatedAtAndUser(any(), any(User.class));
            Assertions.assertEquals(1,result.size());
            Assertions.assertEquals(0,result.get(0).getBookmark().getCount());
            Assertions.assertTrue(result.get(0).getBookmark().getNews().isEmpty());
            Assertions.assertEquals(1, result.get(0).getHistory().getCount());
            Assertions.assertEquals(newsId, result.get(0).getHistory().getNews().get(0).getId());
        }
    }
}