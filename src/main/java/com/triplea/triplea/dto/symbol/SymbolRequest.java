package com.triplea.triplea.dto.symbol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SymbolRequest {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoyaBuzz {
        private Double sentiment;
        private Integer count;
        private Integer positiveCount;
        private Integer negativeCount;
        private String publishedDate;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuzzDuration{
        private BuzzDatas buzzDatas;
        private AvgSentiment avgSentiment;
        private CompanyInfo companyInfo;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BuzzDatas{
            private Double sentiment;
            private Integer count;
            private Integer positiveCount;
            private Integer negativeCount;
            private String publishedDate;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AvgSentiment{
            private Double sentiment;
            private Integer count;
            private Integer positiveCount;
            private Integer negativeCount;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CompanyInfo{
            private String companyName;
            private String symbol;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TiingoSymbol {
        private String ticker;
        private String name;
        private String description;
        private String startDate;
        private String endDate;
        private String exchangeCode;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoyaSymbol {
        private Long id;
        private String symbol;
        private String companyName;
        private String exchange;
        private String industry;
        private String website;
        private String description;
        private String CEO;
        private String issueType;
        private String sector;
        private String logo;
        private String marketType;
    }
}
