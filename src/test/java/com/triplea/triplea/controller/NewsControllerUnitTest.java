package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.NewsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Import({MySecurityConfig.class, MyJwtProvider.class})
@WebMvcTest(NewsController.class)
public class NewsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    private final MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

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

    @Test
    @DisplayName("뉴스 조회(키워드)")
    void newsKeyword() throws Exception {
        //given
        String keyword = "solutions";
        Integer size = 10;
        Long page = 0L;
        String accessToken = MyJwtProvider.create(user);
        //when
        when(newsService.getNewsByKeyword(anyString(), anyInt(), anyLong(), any(User.class)))
                .thenReturn(new NewsResponse.News(keyword, null, new ArrayList<>()));
        //then
        mockMvc.perform(get("/api/news/keyword?keyword=" + keyword + "&size=" + size + "&page=" + page)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("뉴스 조회(카테고리)")
    void newsCategory() throws Exception {
        //given
        Long categoryId = 1L;
        String category = "News";
        Integer size = 10;
        Long page = 0L;
        String accessToken = MyJwtProvider.create(user);
        //when
        when(newsService.getNewsByKeyword(anyString(), anyInt(), anyLong(), any(User.class)))
                .thenReturn(new NewsResponse.News(category, null, new ArrayList<>()));
        //then
        mockMvc.perform(get("/api/news/category/" + categoryId + "?size=" + size + "&page=" + page)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("뉴스 상세 조회")
    void newsDetails() throws Exception {
        //given
        Long newsId = 1L;
        String accessToken = MyJwtProvider.create(user);
        //when
        NewsResponse.Details details = NewsResponse.Details.builder()
                .user(null)
                .details(new ApiResponse.Details())
                .symbol(null)
                .eng(null)
                .kor(null)
                .category(null)
                .bookmark(null)
                .build();
        when(newsService.getNewsDetails(anyLong(), any(User.class)))
                .thenReturn(details);
        //then
        mockMvc.perform(get("/api/news/" + newsId)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Nested
    @DisplayName("히스토리 조회")
    class History {
        @Test
        @DisplayName("성공")
        void test1() throws Exception {
            //given
            int year = 2023;
            int month = 6;
            String accessToken = MyJwtProvider.create(user);
            //when
            when(newsService.getHistory(anyInt(), anyInt(), any(User.class))).thenReturn(Collections.emptyList());
            //then
            mockMvc.perform(get("/api/history?year=" + year + "&month=" + month)
                            .with(csrf())
                            .header(MyJwtProvider.HEADER, accessToken))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("실패1: year null")
        void test2() throws Exception {
            //given
            int month = 6;
            String accessToken = MyJwtProvider.create(user);
            //when
            when(newsService.getHistory(anyInt(), anyInt(), any(User.class))).thenReturn(Collections.emptyList());
            //then
            mockMvc.perform(get("/api/history?month=" + month)
                            .with(csrf())
                            .header(MyJwtProvider.HEADER, accessToken))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andReturn();
        }

        @Test
        @DisplayName("실패1: month null")
        void test3() throws Exception {
            //given
            int year = 2023;
            String accessToken = MyJwtProvider.create(user);
            //when
            when(newsService.getHistory(anyInt(), anyInt(), any(User.class))).thenReturn(Collections.emptyList());
            //then
            mockMvc.perform(get("/api/history?year=" + year)
                            .with(csrf())
                            .header(MyJwtProvider.HEADER, accessToken))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andReturn();
        }
    }
}
