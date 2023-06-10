package com.triplea.triplea.dto.bookmark;

import lombok.Getter;

public class BookmarkResponse {

    @Getter
    public static class BookmarkSymbolDTO {
        private Long symbolId;
        private String symbol;
        private String companyName;
        private String sector;
        private String logo;
        private String marketType;

        public BookmarkSymbolDTO(Long symbolId, String symbol, String companyName, String sector, String logo, String marketType) {
            this.symbolId = symbolId;
            this.symbol = symbol;
            this.companyName = companyName;
            this.sector = sector;
            this.logo = logo;
            this.marketType = marketType;
        }
    }
}
