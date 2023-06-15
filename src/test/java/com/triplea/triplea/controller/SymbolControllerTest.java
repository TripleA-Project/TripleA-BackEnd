package com.triplea.triplea.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.SymbolService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("심볼 API")
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SymbolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SymbolService symbolService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        String email = "dotori@nate.com";
        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser(email, "dotori");
        User userPS = userRepository.save(user);
    }

    @DisplayName("심볼 검색")

    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getSymbols() throws Exception {

        SymbolResponse.SymbolDTO symbolDTO = new SymbolResponse.SymbolDTO(1L, "aapl", "", "", "", "");
        List<SymbolResponse.SymbolDTO> symbolDTOList = new ArrayList<>();
        symbolDTOList.add(symbolDTO);
        when(symbolService.getSymbol(anyString())).thenReturn(symbolDTOList);

        ResultActions resultActions = mockMvc.perform(get("/api/symbol")
                        .param("symbol", "aapl")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseBodyJson = objectMapper.readTree(responseBody);

        Assertions.assertThat(responseBodyJson).isNotNull();
        JsonNode dataNode = responseBodyJson.get("data");
        Assertions.assertThat(dataNode.get(0).get("symbol").equals("aapl"));
    }
}
