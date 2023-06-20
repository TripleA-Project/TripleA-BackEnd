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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Index{
        private Stock nasdaq;
        private Stock dowJones;
        private Stock sp500;
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Stock{
            private String symbol;
            private String name;
            private String price;
            private String percent;
            private String today;
        }
    }
}
