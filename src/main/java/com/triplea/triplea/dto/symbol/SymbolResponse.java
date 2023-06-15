package com.triplea.triplea.dto.symbol;

import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.news.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SymbolResponse {


    @Getter
    @AllArgsConstructor
    public static class SymbolDTO{
        private Long symbolId;
        private String symbol;
        private String companyName;
        private String sector;
        private String logo;
        private String marketType;

        public SymbolDTO(ApiResponse.BookmarkSymbolDTO dto){
            this.symbolId = dto.getId();
            this.symbol = dto.getSymbol();
            this.companyName = dto.getCompanyName();
            this.sector = dto.getSector();
            if(dto.getLogo() != null)
                this.logo = dto.getLogo();
            else
                this.logo = LogoUtil.makeLogo(this.symbol);
            this.marketType = dto.getMarketType();
        }
    }
}
