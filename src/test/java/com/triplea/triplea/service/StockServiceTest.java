package com.triplea.triplea.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.stock.StockResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


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
}