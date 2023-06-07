package com.triplea.triplea.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.bookmark.BookmarkResponse.BookmarkDTO;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.NewsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("뉴스 API")
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private NewsService newsService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        String email = "dotori@nate.com";
        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser(email, "dotori");
        userRepository.save(user);
    }

    @DisplayName("전체 뉴스 조회")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getGlobalNews() throws Exception {

        NewsResponse.GNewsDTO gNewsDTO = new NewsResponse.GNewsDTO();
        ApiResponse.Data data = new ApiResponse.Data();
        data.setSymbol("EZU");
        data.setSource("talkmarkets.com");
        BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(1, true);
        NewsResponse.NewsDTO newsDTO = new NewsResponse.NewsDTO(data, bookmarkDTO);
        List<NewsResponse.NewsDTO> list = new ArrayList<>();
        list.add(newsDTO);
        gNewsDTO.setNews(list);
        gNewsDTO.setNextPage(12345L);

        Mockito.when(newsService.searchAllNews(Mockito.any(User.class), Mockito.any(Pageable.class))).thenReturn(gNewsDTO);

        ResultActions resultActions = mockMvc.perform(get("/api/news/latest?size=10&page=5")
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");

        Assertions.assertThat(dataNode.get("nextPage").asLong()).isEqualTo(12345L);
        JsonNode source = dataNode.get("news").get(0).get("source");
        Assertions.assertThat(source.asText()).isEqualTo("talkmarkets.com");

    }

    @DisplayName("심볼 뉴스 검색")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getSymbolNews() throws Exception {
        NewsResponse.GNewsDTO gNewsDTO = new NewsResponse.GNewsDTO();
        ApiResponse.Data data = new ApiResponse.Data();
        data.setSymbol("EZU");
        data.setSource("talkmarkets.com");
        BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(1, true);
        NewsResponse.NewsDTO newsDTO = new NewsResponse.NewsDTO(data, bookmarkDTO);
        List<NewsResponse.NewsDTO> list = new ArrayList<>();
        list.add(newsDTO);
        gNewsDTO.setNews(list);
        gNewsDTO.setNextPage(12345L);

        Mockito.when(newsService.searchSymbolNews(Mockito.any(User.class), Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(gNewsDTO);

        ResultActions resultActions = mockMvc.perform(get("/api/news?symbol=EZU&size=10&page=5")
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");

        Assertions.assertThat(dataNode.get("nextPage").asLong()).isEqualTo(12345L);
        JsonNode source = dataNode.get("news").get(0).get("source");
        Assertions.assertThat(source.asText()).isEqualTo("talkmarkets.com");
    }
}