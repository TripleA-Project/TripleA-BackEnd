package com.triplea.triplea.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class StockServiceTest {
    @InjectMocks
    private StockService stockService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ObjectMapper OM;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private StepPaySubscriber subscriber;
    @Mock
    private RestTemplate restTemplate;

    @Nested
    @DisplayName("주가 지수 조회")
    class Index {
        @Test
        @DisplayName("성공")
        void test1() throws JsonProcessingException {
            //given
            //when
            String serialize = "serialize";
            StockResponse.Index.Stock stock = StockResponse.Index.Stock.builder()
                    .name("name")
                    .symbol("symbol")
                    .today("+today")
                    .price("10,000")
                    .percent("-10%")
                    .build();
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(serialize);
            when(OM.readValue(anyString(), eq(StockResponse.Index.Stock.class))).thenReturn(stock);
            StockResponse.Index result = stockService.getStockIndex();
            //then
            verify(redisTemplate.opsForValue(), times(3)).get(anyString());
            verify(OM, times(3)).readValue(anyString(), eq(StockResponse.Index.Stock.class));
            Assertions.assertEquals(stock.getName(), result.getNasdaq().getName());
            Assertions.assertEquals(stock.getName(), result.getDowJones().getName());
            Assertions.assertEquals(stock.getName(), result.getSp500().getName());
            Assertions.assertDoesNotThrow(() -> stockService.getStockIndex());
        }

        @Test
        @DisplayName("실패: redis 데이터 없음")
        void test2() throws JsonProcessingException {
            //given
            //when
            String serialize = null;
            StockResponse.Index.Stock stock = StockResponse.Index.Stock.builder()
                    .name("name")
                    .symbol("symbol")
                    .today("+today")
                    .price("10,000")
                    .percent("-10%")
                    .build();
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(redisTemplate.opsForValue().get(anyString())).thenReturn(serialize);
            when(OM.readValue(anyString(), eq(StockResponse.Index.Stock.class))).thenReturn(stock);
            //then
            Assertions.assertThrows(Exception500.class, () -> stockService.getStockIndex());
        }
    }

    @Nested
    @DisplayName("Stock Chart Retrieval")
    class GetChart {
        @Test
        @DisplayName("Success Case")
        void testSuccess() throws IOException {
            // given
            String symbol = "AAPL";
            String startDate = "2023-06-01";
            String endDate = "2023-06-05";
            String resampleFreq = "daily";

            // Mock external API calls and dependencies
            Customer mockCustomer = new Customer();
            mockCustomer.subscribe(1L);
            DummyEntity dummy = new DummyEntity();
            User user = dummy.newMockUser(1L, "dotori@nate.com", "dotori");

            when(customerRepository.findCustomerByUserId(anyLong())).thenReturn(Optional.of(mockCustomer));
            when(subscriber.isSubscribe(anyLong())).thenReturn(true);

            ApiResponse.Tiingo[] mockTiingoResponse = new ApiResponse.Tiingo[1]; // Populate this with mock data
            ResponseEntity<ApiResponse.Tiingo[]> mockedResponseEntity = new ResponseEntity<>(mockTiingoResponse, HttpStatus.OK);

            when(restTemplate.getForEntity(anyString(), eq(ApiResponse.Tiingo[].class))).thenReturn(mockedResponseEntity);

            StockResponse.GlobalBuzzDuration mockBuzzResponse = new StockResponse.GlobalBuzzDuration(); // Populate this with mock data
            ResponseEntity<StockResponse.GlobalBuzzDuration> mockedBuzzResponseEntity = new ResponseEntity<>(mockBuzzResponse, HttpStatus.OK);

            when(restTemplate.getForEntity(anyString(), eq(StockResponse.GlobalBuzzDuration.class))).thenReturn(mockedBuzzResponseEntity);

            // when
            StockResponse.StockInfoDTO result = stockService.getChart(symbol, startDate, endDate, resampleFreq, user);

            // then
            // Verify the mocked calls and add assertions to check the expected result
            verify(customerRepository, times(1)).findCustomerByUserId(anyLong());
            verify(subscriber, times(1)).isSubscribe(anyLong());
            verify(restTemplate, times(2)).getForEntity(anyString(), any());

        }
    }
}