package com.triplea.triplea.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.model.bookmark.BookmarkSymbol;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.symbol.Symbol;
import com.triplea.triplea.model.symbol.SymbolRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.BookmarkSymbolService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DisplayName("북마크심볼 API")
//@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookmarkSymbolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
    @MockBean
    private BookmarkSymbolService bookmarkSymbolService;
    //// --->

    @Autowired
    private BookmarkSymbolRepository bookmarkSymbolRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {

        bookmarkSymbolRepository.deleteAll();
        symbolRepository.deleteAll();
        userRepository.deleteAll();

        String email = "dotori@nate.com";
        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser(email, "dotori");
        User userPS = userRepository.save(user);
    }

    @DisplayName("북마크심볼 조회")

    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void recommendedBookmarkSymbol() throws Exception {

        Optional<User> dotoriPS = userRepository.findUserByEmail("dotori@nate.com");

        DummyEntity dummy = new DummyEntity();
        User potato = dummy.newUser("potato@nate.com", "potato");
        User potatoPS = userRepository.save(potato);

        User kiwi = dummy.newUser("kiwi@nate.com", "kiwi");
        User kiwiPS = userRepository.save(potato);

        Symbol symbol = new Symbol(0L, "AA");//3
        Symbol symbol2 = new Symbol(0L, "BB");//2
        Symbol symbol3 = new Symbol(0L, "CC");//1
        Symbol symbolPS = symbolRepository.save(symbol);
        Symbol symbol2PS = symbolRepository.save(symbol2);
        Symbol symbol3PS = symbolRepository.save(symbol3);

        //dotori
        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbolPS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol);

        BookmarkSymbol bookmarkSymbol2 = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbol2PS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol2);

        BookmarkSymbol bookmarkSymbol3 = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbol3PS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol3);

        //potato
        BookmarkSymbol bookmarkSymbol4 = BookmarkSymbol.builder()
                .user(potatoPS)
                .symbolId(symbolPS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol4);

        BookmarkSymbol bookmarkSymbol5 = BookmarkSymbol.builder()
                .user(potatoPS)
                .symbolId(symbol2PS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol5);

        //kiwi
        BookmarkSymbol bookmarkSymbol6 = BookmarkSymbol.builder()
                .user(kiwiPS)
                .symbolId(symbolPS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol6);

        //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

        ApiResponse.Tiingo[] tiingoList = { ApiResponse.Tiingo.builder().build(), ApiResponse.Tiingo.builder().build()};
        BookmarkResponse.Price price = new BookmarkResponse.Price(tiingoList[0], tiingoList[1]);

                BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                551013L, "AA", "Alcoa Corp", null, LogoUtil.makeLogo("AA"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO);

        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO2 = new BookmarkResponse.BookmarkSymbolDTO(
                550840L, "BB", "BlackBerry Ltd", null, LogoUtil.makeLogo("BB"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO2);

        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO3 = new BookmarkResponse.BookmarkSymbolDTO(
                552917L, "CC", "Chemours Company", null, LogoUtil.makeLogo("CC"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO3);

        Mockito.when(bookmarkSymbolService.recommendBookmarkSymbol()).thenReturn(bookmarkSymbolDTOList);
        //// --->

        ResultActions resultActions = mockMvc
                .perform(get("/api/symbol/recommand").contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");

        Assertions.assertThat(dataNode.get(0).get("symbolId").asInt() == 551013);
        Assertions.assertThat(dataNode.get(1).get("symbolId").asInt() == 550840);
        Assertions.assertThat(dataNode.get(2).get("symbolId").asInt() == 552917);

    }

    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void likedBookmarkSymbol() throws Exception {

        Optional<User> dotoriPS = userRepository.findUserByEmail("dotori@nate.com");

        Symbol symbol = new Symbol(0L, "AA");//3
        Symbol symbol2 = new Symbol(0L, "BB");//2
        Symbol symbol3 = new Symbol(0L, "CC");//1
        Symbol symbolPS = symbolRepository.save(symbol);
        Symbol symbol2PS = symbolRepository.save(symbol2);
        Symbol symbol3PS = symbolRepository.save(symbol3);

        //dotori
        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbolPS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol);

        BookmarkSymbol bookmarkSymbol2 = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbol2PS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol2);

        BookmarkSymbol bookmarkSymbol3 = BookmarkSymbol.builder()
                .user(dotoriPS.get())
                .symbolId(symbol3PS.getId())
                .isDeleted(false)
                .build();
        bookmarkSymbolRepository.save(bookmarkSymbol3);

        //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

        ApiResponse.Tiingo[] tiingoList = { ApiResponse.Tiingo.builder().build(), ApiResponse.Tiingo.builder().build()};
        BookmarkResponse.Price price = new BookmarkResponse.Price(tiingoList[0], tiingoList[1]);

        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                551013L, "AA", "Alcoa Corp", null, LogoUtil.makeLogo("AA"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO);

        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO2 = new BookmarkResponse.BookmarkSymbolDTO(
                550840L, "BB", "BlackBerry Ltd", null, LogoUtil.makeLogo("BB"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO2);

        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO3 = new BookmarkResponse.BookmarkSymbolDTO(
                552917L, "CC", "Chemours Company", null, LogoUtil.makeLogo("CC"),
                "NYSE", price);
        bookmarkSymbolDTOList.add(bookmarkSymbolDTO3);

        Mockito.when(bookmarkSymbolService.getLikedBookmarkSymbol(any())).thenReturn(bookmarkSymbolDTOList);
        //// --->

        ResultActions resultActions = mockMvc
                .perform(get("/api/symbol/like").contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");

        Assertions.assertThat(dataNode.get(0).get("symbolId").asInt() == 551013);
        Assertions.assertThat(dataNode.get(1).get("symbolId").asInt() == 550840);
        Assertions.assertThat(dataNode.get(2).get("symbolId").asInt() == 552917);
    }
}
