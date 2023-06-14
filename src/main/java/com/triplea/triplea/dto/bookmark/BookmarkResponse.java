package com.triplea.triplea.dto.bookmark;

import com.triplea.triplea.dto.news.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkResponse {

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price{
        private ApiResponse.Tiingo today;
        private ApiResponse.Tiingo yesterday;
    }

    @Getter
    public static class BookmarkDTO {

        private Integer count;
        private Boolean isBookmark;

        @Builder
        public BookmarkDTO(Integer count, Boolean isBookmark) {
            this.count = count;
            this.isBookmark = isBookmark;
        }
    }

    @Getter
    public static class BookmarkSymbolDTO {
        private Long symbolId;
        private String symbol;
        private String companyName;
        private String sector;
        private String logo;
        private String marketType;

        private Price price;

        public BookmarkSymbolDTO(Long symbolId, String symbol, String companyName, String sector, String logo, String marketType, Price price) {
            this.symbolId = symbolId;
            this.symbol = symbol;
            this.companyName = companyName;
            this.sector = sector;
            this.logo = logo;
            this.marketType = marketType;
            this.price = price;
        }
    }
}
