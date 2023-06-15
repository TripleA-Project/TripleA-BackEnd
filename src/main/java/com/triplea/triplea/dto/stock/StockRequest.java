package com.triplea.triplea.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StockRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TiingoStock{
        private String date;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Long volume;
        private Double adjOpen;
        private Double adjHigh;
        private Double adjLow;
        private Double adjClose;
        private Long adjVolume;
        private Double divCash;
        private Double splitFactor;
    }
}
