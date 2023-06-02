package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.model.bookmark.Bookmark;
import com.triplea.triplea.model.bookmark.BookmarkRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.BookmarkService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("북마크 API")
//@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {

        bookmarkRepository.deleteAll();
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

        List<Bookmark> bookmarkList = bookmarkRepository.findByNewsId(newsId);
        Assertions.assertThat(bookmarkList.size() == 1);
        Assertions.assertThat(bookmarkList.get(0).getNewsId() == newsId);
    }

    @DisplayName("북마크 삭제")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteBookmark() throws Exception {

        Long newsId = 999L;

        Optional<User> userPS = userRepository.findUserByEmail("dotori@nate.com");
        Bookmark bookmark = Bookmark.builder()
                .user(userPS.get())
                .newsId(newsId)
                .isDeleted(false)
                .build();
        Bookmark bookmarkPS = bookmarkRepository.save(bookmark);

        ResultActions resultActions = mockMvc
                .perform(delete("/api/news/" + newsId).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        List<Bookmark> all = bookmarkRepository.findAll();
        Assertions.assertThat(all.get(0).isDeleted() == true);
        resultActions.andExpect(status().isOk());

    }
}
