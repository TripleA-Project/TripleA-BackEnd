package com.triplea.triplea.controller;

import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import com.triplea.triplea.service.NewsService;
import com.triplea.triplea.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static net.bytebuddy.matcher.ElementMatchers.any;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

@DisplayName("주식 차트 API")
//@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;

    //<--- API 호출 피하기위한 세팅
    @MockBean
    private StockService stockService;
    //--->

    @MockBean
    private StepPaySubscriber subscriber;

    @BeforeEach
    public void setUp() {

        customerRepository.deleteAll();
        userRepository.deleteAll();

        String email = "dotori@nate.com";
        DummyEntity dummy = new DummyEntity();
        User user = dummy.newUser(email, "dotori");
        User userPS = userRepository.save(user);

    }

    @DisplayName("주식차트")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getChart() throws Exception {

        Optional<User> opUser = userRepository.findUserByEmail("dotori@nate.com");

        Customer customer = new Customer(1L, "", opUser.get());
        customerRepository.save(customer);

        // given
        String symbol = "AAPL";
        String startDate = "2023-06-01";
        String endDate = "2023-06-05";
        String resampleFreq = "daily";

        Mockito.when(subscriber.isSubscribe(anyLong())).thenReturn(true);

        ////
        ApiResponse.Tiingo tiingo = ApiResponse.Tiingo.builder()
                .date("2023-06-01T00:00:00.000Z")
                .open(150.0)
                .high(155.0)
                .low(145.0)
                .close(150.5)
                .volume(10000L)
                .adjOpen(150.0)
                .adjHigh(155.0)
                .adjLow(145.0)
                .adjClose(150.5)
                .adjVolume(10000L)
                .divCash(0.0)
                .splitFactor(1.0)
                .build();

        List<StockResponse.Chart> chartList = new ArrayList<>();
        StockResponse.Chart chart = new StockResponse.Chart(tiingo);
        chartList.add(chart);

        StockResponse.StockInfoDTO stockInfoDTO = new StockResponse.StockInfoDTO("BASIC", symbol, "Apple Inc.", chartList);

        User user = opUser.get();

        Mockito.when(stockService.getChart(symbol, startDate, endDate, resampleFreq, (User) any()))
                .thenReturn(stockInfoDTO);


        ////


        ResultActions resultActions = mockMvc.perform(get("/api/stocks")
                .param("symbol", symbol)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("resampleFreq", resampleFreq)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.membership").value("BASIC"))
                .andExpect(jsonPath("$.data.symbol").value(symbol))
                .andExpect(jsonPath("$.data.companyName").value("Apple Inc."));

    }
}
