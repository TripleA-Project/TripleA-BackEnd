package com.triplea.triplea.dto.stock;

import com.triplea.triplea.dto.news.ApiResponse;
import lombok.*;

import java.util.List;

public class StockResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuzzChart {
        private List<BuzzData> buzzDataList;
        private List<Chart> chartList;
    }

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
        private List<Stock> stocks;
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

    @Setter
    @Getter
    public static class Chart{
        private String date;
        private double close;
        private double high;
        private double low;
        private double open;
        private long volume;
        private double adjClose;
        private double adjHigh;
        private double adjLow;
        private double adjOpen;
        private long adjVolume;
        private double divCash;
        private double splitFactor;
        private double sentiment;
        private long buzz;

        public Chart(ApiResponse.Tiingo tiingo) {
            this.date = tiingo.getDate();
            this.close = tiingo.getClose();
            this.high = tiingo.getHigh();
            this.low = tiingo.getLow();
            this.open = tiingo.getOpen();
            this.volume = tiingo.getVolume();
            this.adjClose = tiingo.getAdjClose();
            this.adjHigh = tiingo.getAdjHigh();
            this.adjLow = tiingo.getAdjLow();
            this.adjOpen = tiingo.getAdjOpen();
            this.adjVolume = tiingo.getAdjVolume();
            this.divCash = tiingo.getDivCash();
            this.splitFactor = tiingo.getSplitFactor();
        }
    }

    @Getter
    @Setter
    public static class StockInfoDTO{

        private String membership;
        private String symbol;
        private String companyName;

        private List<Chart> charts;

        private List<BuzzData> buzzDataList;

        public StockInfoDTO(String membership, String symbol, String companyName, List<Chart> charts){
            this.membership = membership;
            this.symbol = symbol;
            this.companyName = companyName;
            this.charts = charts;
        }

    }

    @Getter
    public static class BuzzData{
        private Double sentiment;
        private Integer count;
        private Integer positiveCount;
        private Integer negativeCount;
        private String publishedDate;
    }

    @Getter
    public static class AvgSentiment{
        private Double sentiment;
        private Integer count;
        private Integer positiveCount;
        private Integer negativeCount;
    }

    @Getter
    public static class CompanyInfo{
        private String companyName;
        private String symbol;
    }


    @Getter
    public static class GlobalBuzzDuration{
        private List<BuzzData> buzzDatas;
        private List<AvgSentiment> avgSentiment;
        private List<CompanyInfo> companyInfo;
    }
}
