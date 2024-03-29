//package com.triplea.triplea.controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
//import com.triplea.triplea.core.dummy.DummyEntity;
//import com.triplea.triplea.core.util.LogoUtil;
//import com.triplea.triplea.dto.bookmark.BookmarkResponse;
//import com.triplea.triplea.dto.news.ApiResponse;
//import com.triplea.triplea.model.bookmark.BookmarkSymbol;
//import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
//import com.triplea.triplea.model.user.User;
//import com.triplea.triplea.model.user.UserRepository;
//import com.triplea.triplea.service.BookmarkSymbolService;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.TestExecutionEvent;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//@DisplayName("북마크심볼 API")
////@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//public class BookmarkSymbolControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
//    @MockBean
//    private BookmarkSymbolService bookmarkSymbolService;
//    //// --->
//
//    @Autowired
//    private BookmarkSymbolRepository bookmarkSymbolRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @BeforeEach
//    public void setup() {
//
//        bookmarkSymbolRepository.deleteAll();
//        userRepository.deleteAll();
//
//        String email = "dotori@nate.com";
//        DummyEntity dummy = new DummyEntity();
//        User user = dummy.newUser(email, "dotori");
//        User userPS = userRepository.save(user);
//    }
//
//    @DisplayName("북마크심볼 조회")
//
//    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void recommendedBookmarkSymbol() throws Exception {
//
//        Optional<User> dotoriPS = userRepository.findUserByEmail("dotori@nate.com");
//
//        DummyEntity dummy = new DummyEntity();
//        User potato = dummy.newUser("potato@nate.com", "potato");
//        User potatoPS = userRepository.save(potato);
//
//        User kiwi = dummy.newUser("kiwi@nate.com", "kiwi");
//        User kiwiPS = userRepository.save(potato);
//
//        String symbol = "AA";//3
//        String symbol2 = "BB";//2
//        String symbol3 = "CC";//1
//
//        //dotori
//        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol);
//
//        BookmarkSymbol bookmarkSymbol2 = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol2)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol2);
//
//        BookmarkSymbol bookmarkSymbol3 = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol3)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol3);
//
//        //potato
//        BookmarkSymbol bookmarkSymbol4 = BookmarkSymbol.builder()
//                .user(potatoPS)
//                .symbol(symbol)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol4);
//
//        BookmarkSymbol bookmarkSymbol5 = BookmarkSymbol.builder()
//                .user(potatoPS)
//                .symbol(symbol2)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol5);
//
//        //kiwi
//        BookmarkSymbol bookmarkSymbol6 = BookmarkSymbol.builder()
//                .user(kiwiPS)
//                .symbol(symbol)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol6);
//
//        //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
//        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();
//
//        ApiResponse.Tiingo tiingo = ApiResponse.Tiingo.builder()
//                .date("2023-06-01T00:00:00.000Z")
//                .open(150.0)
//                .high(155.0)
//                .low(145.0)
//                .close(150.5)
//                .volume(10000L)
//                .adjOpen(150.0)
//                .adjHigh(155.0)
//                .adjLow(145.0)
//                .adjClose(150.5)
//                .adjVolume(10000L)
//                .divCash(0.0)
//                .splitFactor(1.0)
//                .build();
//
//        ApiResponse.Tiingo[] tiingoList = { tiingo, tiingo};
//        BookmarkResponse.Price price = new BookmarkResponse.Price(tiingoList[0], tiingoList[1]);
//
//                BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
//                551013L, "AA", "Alcoa Corp", null, LogoUtil.makeLogo("AA"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
//
//        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO2 = new BookmarkResponse.BookmarkSymbolDTO(
//                550840L, "BB", "BlackBerry Ltd", null, LogoUtil.makeLogo("BB"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO2);
//
//        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO3 = new BookmarkResponse.BookmarkSymbolDTO(
//                552917L, "CC", "Chemours Company", null, LogoUtil.makeLogo("CC"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO3);
//
//        Mockito.when(bookmarkSymbolService.recommendBookmarkSymbol()).thenReturn(bookmarkSymbolDTOList);
//        //// --->
//
//        ResultActions resultActions = mockMvc
//                .perform(get("/api/symbol/recommand").contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode responseBodyJson = objectMapper.readTree(responseBody);
//
//        Assertions.assertThat(responseBodyJson).isNotNull();
//        JsonNode dataNode = responseBodyJson.get("data");
//
//        Assertions.assertThat(dataNode.get(0).get("symbolId").asInt() == 551013);
//        Assertions.assertThat(dataNode.get(1).get("symbolId").asInt() == 550840);
//        Assertions.assertThat(dataNode.get(2).get("symbolId").asInt() == 552917);
//
//    }
//
//    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void likedBookmarkSymbol() throws Exception {
//
//        Optional<User> dotoriPS = userRepository.findUserByEmail("dotori@nate.com");
//
//        String symbol = "AA";//3
//        String symbol2 = "BB";//2
//        String symbol3 = "CC";//1
//
//        //dotori
//        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol);
//
//        BookmarkSymbol bookmarkSymbol2 = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol2)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol2);
//
//        BookmarkSymbol bookmarkSymbol3 = BookmarkSymbol.builder()
//                .user(dotoriPS.get())
//                .symbol(symbol3)
//                .isDeleted(false)
//                .build();
//        bookmarkSymbolRepository.save(bookmarkSymbol3);
//
//        //// <---모야 API 호출 하지 않고 테스트 하기 위한 세팅
//        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();
//
//        ApiResponse.Tiingo tiingo = ApiResponse.Tiingo.builder()
//                .date("2023-06-01T00:00:00.000Z")
//                .open(150.0)
//                .high(155.0)
//                .low(145.0)
//                .close(150.5)
//                .volume(10000L)
//                .adjOpen(150.0)
//                .adjHigh(155.0)
//                .adjLow(145.0)
//                .adjClose(150.5)
//                .adjVolume(10000L)
//                .divCash(0.0)
//                .splitFactor(1.0)
//                .build();
//
//        ApiResponse.Tiingo[] tiingoList = { tiingo, tiingo};
//        BookmarkResponse.Price price = new BookmarkResponse.Price(tiingoList[0], tiingoList[1]);
//
//        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
//                551013L, "AA", "Alcoa Corp", null, LogoUtil.makeLogo("AA"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
//
//        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO2 = new BookmarkResponse.BookmarkSymbolDTO(
//                550840L, "BB", "BlackBerry Ltd", null, LogoUtil.makeLogo("BB"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO2);
//
//        BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO3 = new BookmarkResponse.BookmarkSymbolDTO(
//                552917L, "CC", "Chemours Company", null, LogoUtil.makeLogo("CC"),
//                "NYSE", price);
//        bookmarkSymbolDTOList.add(bookmarkSymbolDTO3);
//
//        Mockito.when(bookmarkSymbolService.getLikedBookmarkSymbol(any())).thenReturn(bookmarkSymbolDTOList);
//        //// --->
//
//        ResultActions resultActions = mockMvc
//                .perform(get("/api/auth/symbol/like").contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode responseBodyJson = objectMapper.readTree(responseBody);
//
//        Assertions.assertThat(responseBodyJson).isNotNull();
//        JsonNode dataNode = responseBodyJson.get("data");
//
//        Assertions.assertThat(dataNode.get(0).get("symbolId").asInt() == 551013);
//        Assertions.assertThat(dataNode.get(1).get("symbolId").asInt() == 550840);
//        Assertions.assertThat(dataNode.get(2).get("symbolId").asInt() == 552917);
//    }
//
//    @Test
//    @DisplayName("관심 심볼 생성")
//    void saveLikeCategory() throws Exception {
//
//        DummyEntity dummy = new DummyEntity();
//        User tester = dummy.newMockUser(1L, "test1@example.com", "tester");
//        User testerPS = userRepository.save(tester);
//
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(testerPS);
//        //when
//
//        //then
//        mockMvc.perform(get("/api/auth/symbol?symbol=AAPL")
//                .with(csrf())
//                .header(MyJwtProvider.HEADER, accessToken))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andReturn();
//    }
//
//    @Test
//    @DisplayName("관심 심볼 삭제")
//    void deleteLikeCategory() throws Exception {
//
//        DummyEntity dummy = new DummyEntity();
//        User tester = dummy.newMockUser(1L, "test1@example.com", "tester");
//        User testerPS = userRepository.save(tester);
//        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder().id(1L).symbol("AA").isDeleted(false).user(testerPS).build();
//        bookmarkSymbolRepository.save(bookmarkSymbol);
//
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(testerPS);
//        //when
//
//        //then
//        mockMvc.perform(delete("/api/auth/symbol/1")
//                .with(csrf())
//                .header(MyJwtProvider.HEADER, accessToken))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andReturn();
//    }
//}
