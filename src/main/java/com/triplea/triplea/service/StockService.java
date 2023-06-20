package com.triplea.triplea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.stock.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper OM;

    // 주가 지수 조회
    public StockResponse.Index getStockIndex() {
        String nasdaqSerialize = redisTemplate.opsForValue().get("index_ixic");
        String dowJonesSerialize = redisTemplate.opsForValue().get("index_dji");
        String sp500Serialize = redisTemplate.opsForValue().get("index_gspc");
        try {
            StockResponse.Index.Stock nasdaq = OM.readValue(nasdaqSerialize, StockResponse.Index.Stock.class);
            StockResponse.Index.Stock dowJones = OM.readValue(dowJonesSerialize, StockResponse.Index.Stock.class);
            StockResponse.Index.Stock sp500 = OM.readValue(sp500Serialize, StockResponse.Index.Stock.class);
            return StockResponse.Index.builder()
                    .nasdaq(nasdaq)
                    .dowJones(dowJones)
                    .sp500(sp500)
                    .build();
        } catch (Exception e) {
            throw new Exception500("주가 지수 조회 실패: " + e.getMessage());
        }
    }
}
