package com.triplea.triplea.controller;

import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.BookmarkNewsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("북마크뉴스 API")
//@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookmarkNewsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookmarkNewsService bookmarkNewsService;

    @Autowired
    private BookmarkNewsRepository bookmarkNewsRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {

        bookmarkNewsRepository.deleteAll();
        userRepository.deleteAll();

        String email = "dotori@nate.com";
        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser(email, "dotori");
        User userPS = userRepository.save(user);

    }

    @DisplayName("북마크 추가")

    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void insertBookmark() throws Exception {
        Long newsId = 999L;

        ResultActions resultActions = mockMvc
                .perform(post("/api/news/" + newsId).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        resultActions.andExpect(status().isOk());

        List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findNonDeletedByNewsId(newsId);
        Assertions.assertThat(bookmarkNewsList.size() == 1);
        Assertions.assertThat(bookmarkNewsList.get(0).getNewsId() == newsId);
    }

    @DisplayName("북마크 삭제")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteBookmark() throws Exception {

        Long newsId = 999L;

        Optional<User> userPS = userRepository.findUserByEmail("dotori@nate.com");
        BookmarkNews bookmarkNews = BookmarkNews.builder()
                .user(userPS.get())
                .newsId(newsId)
                .isDeleted(false)
                .build();
        BookmarkNews bookmarkNewsPS = bookmarkNewsRepository.save(bookmarkNews);

        ResultActions resultActions = mockMvc
                .perform(delete("/api/news/" + newsId).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        List<BookmarkNews> all = bookmarkNewsRepository.findAll();
        Assertions.assertThat(all.get(0).isDeleted() == true);
        resultActions.andExpect(status().isOk());

    }
}
