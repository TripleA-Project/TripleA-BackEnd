package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.NewsService;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Import({MySecurityConfig.class, MyJwtProvider.class})
@WebMvcTest(NewsController.class)
public class NewControllerUnitTest {

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
    void test() throws Exception {
        //given
        String keyword = "solutions";
        Integer size = 10;
        Long page = 0L;
        String accessToken = MyJwtProvider.create(user);
        //when
        when(newsService.getNewsByKeyword(anyString(), anyInt(), anyLong(), any(User.class)))
                .thenReturn(new NewsResponse.News(null, new ArrayList<>()));
        //then
        mockMvc.perform(get("/api/news/keyword?keyword=" + keyword + "&size=" + size + "&page=" + page)
                        .with(csrf())
                        .header(MyJwtProvider.HEADER, accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}
