package com.triplea.triplea.controller;


import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Import({MySecurityConfig.class})
@WebMvcTest(StockController.class)
class StockControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StockService stockService;

    @Test
    @DisplayName("주가 지수 조회")
    void getStockIndex() throws Exception {
        //given
        //when
        StockResponse.Index index = StockResponse.Index.builder()
                .nasdaq(null)
                .dowJones(null)
                .sp500(null)
                .build();
        when(stockService.getStockIndex()).thenReturn(index);
        //then
        mockMvc.perform(get("/api/stocks/index")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("주식 차트 조회")
    void getSymbol() throws Exception {
        //given
        String symbol = "AAPL";
        String startDate = "20230101";
        String endDate = "20240101";
        String resampleFreq = "daily";

        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser("dotori@nate.com", "dotori");

        List<StockResponse.Chart> charts = new ArrayList<>();
        ApiResponse.Tiingo tiingo = new ApiResponse.Tiingo("2022-06-03T00:00:00.000Z", 0D, 0D, 0D, 0D, 0L,0D,0D, 0D, 0D, 0L, 0D, 0D);
        StockResponse.Chart chart = new StockResponse.Chart(tiingo);
        charts.add(chart);
        StockResponse.StockInfoDTO stockInfoDTO = new StockResponse.StockInfoDTO("membership", symbol, "companyName", charts);

        when(stockService.getChart(symbol, startDate, endDate, resampleFreq, user)).thenReturn(stockInfoDTO);

        //when & then
        ResultActions resultActions = mockMvc.perform(get("/api/stocks")
                .with(csrf())
                .param("symbol", symbol)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("resampleFreq", resampleFreq)
                .with(user(new MyUserDetails(user))));

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.membership").value("membership"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.symbol").value(symbol))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.companyName").value("companyName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.charts[0].date").value("2022-06-03T00:00:00.000Z"));
    }
}