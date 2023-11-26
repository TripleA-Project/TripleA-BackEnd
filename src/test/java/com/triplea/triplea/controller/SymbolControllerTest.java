//package com.triplea.triplea.controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.triplea.triplea.core.dummy.DummyEntity;
//import com.triplea.triplea.core.util.LogoUtil;
//import com.triplea.triplea.dto.bookmark.BookmarkResponse;
//import com.triplea.triplea.dto.news.ApiResponse;
//import com.triplea.triplea.dto.symbol.SymbolResponse;
//import com.triplea.triplea.model.user.User;
//import com.triplea.triplea.model.user.UserRepository;
//import com.triplea.triplea.service.SymbolService;
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
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("심볼 API")
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//public class SymbolControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    //<-- 외부 API 호출 안하는 세팅
//    @MockBean
//    private SymbolService symbolService;
//    //-->
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @BeforeEach
//    public void setup() {
//        userRepository.deleteAll();
//
//        String email = "dotori@nate.com";
//        DummyEntity dummy = new DummyEntity();
//        User user = dummy.newUser(email, "dotori");
//        User userPS = userRepository.save(user);
//    }
//
//    @DisplayName("심볼 검색")
//    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void searchSymbols() throws Exception {
//
//        //<-- 외부 API 호출 안하는 세팅
//        SymbolResponse.SymbolDTO symbolDTO = new SymbolResponse.SymbolDTO(1L, "aapl", "", "", "", "");
//        List<SymbolResponse.SymbolDTO> symbolDTOList = new ArrayList<>();
//        symbolDTOList.add(symbolDTO);
//        when(symbolService.searchSymbol(anyString())).thenReturn(symbolDTOList);
//        //-->
//
//        ResultActions resultActions = mockMvc.perform(get("/api/symbol/search")
//                        .param("symbol", "aapl")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode responseBodyJson = objectMapper.readTree(responseBody);
//
//        Assertions.assertThat(responseBodyJson).isNotNull();
//        JsonNode dataNode = responseBodyJson.get("data");
//        Assertions.assertThat(dataNode.get(0).get("symbol").equals("aapl"));
//    }
//
//    @DisplayName("심볼 조회")
//    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void getSymbols() throws Exception {
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
//        Mockito.when(symbolService.getSymbol(anyString())).thenReturn(bookmarkSymbolDTOList);
//        //// --->
//
//        ResultActions resultActions = mockMvc.perform(get("/api/symbol")
//                        .param("symbol", "AA")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode responseBodyJson = objectMapper.readTree(responseBody);
//
//        Assertions.assertThat(responseBodyJson).isNotNull();
//        JsonNode dataNode = responseBodyJson.get("data");
//        Assertions.assertThat(dataNode.get(0).get("symbol").equals("AA"));
//    }
//}
