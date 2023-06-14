package com.triplea.triplea.dto.symbol;

import com.triplea.triplea.dto.stock.StockResponse;
import lombok.Builder;
import lombok.Getter;

public class SymbolResponse {
    @Getter
    public static class News {
        private String name;
        private String companyName;
        private String sector;
        private String logo;
        private String marketType;
        private StockResponse.Price price;

        @Builder
        public News(SymbolRequest.MoyaSymbol symbol, String logo, StockResponse.Price price) {
            this.name = symbol.getSymbol();
            this.companyName = symbol.getCompanyName();
            this.sector = symbol.getSector();
            this.logo = logo;
            this.marketType = symbol.getMarketType();
            this.price = price;
        }
    }
}
