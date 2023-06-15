package com.triplea.triplea.dto.symbol;

import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SymbolResponse {


    @Getter
    public static class SymbolDTO {
        private Long symbolId;
        private String symbol;

        private String companyName;
        private String sector;
        private String logo;
        private String marketType;

        public SymbolDTO(ApiResponse.BookmarkSymbolDTO dto) {
            this.symbolId = dto.getId();
            this.symbol = dto.getSymbol();
            this.companyName = dto.getCompanyName();
            this.sector = dto.getSector();
            if (dto.getLogo() != null)
                this.logo = dto.getLogo();
            else
                this.logo = LogoUtil.makeLogo(this.symbol);
            this.marketType = dto.getMarketType();
        }

        public SymbolDTO(Long symbolId, String symbol, String companyName, String sector, String logo, String marketType){
            this.symbolId = symbolId;
            this.symbol = symbol;
            this.companyName = companyName;
            this.sector = sector;
            if (logo != null)
                this.logo = logo;
            else
                this.logo = LogoUtil.makeLogo(this.symbol);
            this.marketType = marketType;
        }
    }

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
