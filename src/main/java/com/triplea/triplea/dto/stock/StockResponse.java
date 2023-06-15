package com.triplea.triplea.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StockResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price {
        private StockRequest.TiingoStock today;
        private StockRequest.TiingoStock yesterday;
    }
}
