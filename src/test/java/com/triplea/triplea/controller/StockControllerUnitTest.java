package com.triplea.triplea.controller;

import com.triplea.triplea.core.config.MySecurityConfig;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
}