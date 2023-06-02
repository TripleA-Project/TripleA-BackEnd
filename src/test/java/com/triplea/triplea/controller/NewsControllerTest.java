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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.awt.print.Book;
import java.util.ArrayList;
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

        List<NewsResponse.NewsDTO> mockNewsDTOList = new ArrayList<>();
        ApiResponse.Data data = new ApiResponse.Data();
        data.setSymbol("EZU");
        data.setSource("talkmarkets.com");
        BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(1, true);
        NewsResponse.NewsDTO newsDTO = new NewsResponse.NewsDTO(data, bookmarkDTO);
        mockNewsDTOList.add(newsDTO);

        Mockito.when(newsService.searchAllNews(Mockito.any(User.class))).thenReturn(mockNewsDTOList);

        ResultActions resultActions = mockMvc.perform(get("/api/news/latest")
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");

        JsonNode firstDataNode = dataNode.get(0);
        Assertions.assertThat(firstDataNode).isNotNull();
        Assertions.assertThat(firstDataNode.has("source")).isTrue();
        Assertions.assertThat(firstDataNode.get("source").asText()).isEqualTo("talkmarkets.com");
    }
}