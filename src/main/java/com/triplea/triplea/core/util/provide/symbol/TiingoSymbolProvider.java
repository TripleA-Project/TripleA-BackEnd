package com.triplea.triplea.core.util.provide.symbol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TiingoSymbolProvider extends SymbolProvider{

    @Value("${tiingo.token}")
    private String token;

    @Override
    protected HttpUrl.Builder getUrl(String symbol) {
        HttpUrl.Builder url = HttpUrl.parse("https://api.tiingo.com/tiingo/daily/" + symbol).newBuilder();
        url.addQueryParameter("token", token);
        return url;
    }

    /**
     * Tiingo API 로 symbol(ticker) 조회
     * @param symbol symbol
     * @return TiingoSymbol
     */
    public SymbolRequest.MoyaSymbol getSymbolInfo(String symbol) {
        try (Response response = getSymbol(symbol)) {
            SymbolRequest.TiingoSymbol tiingoSymbol = getTiingoSymbol(response, OM);
            return SymbolRequest.MoyaSymbol.builder()
                    .symbol(tiingoSymbol.getTicker())
                    .companyName(tiingoSymbol.getName())
                    .description(tiingoSymbol.getDescription())
                    .marketType(tiingoSymbol.getExchangeCode())
                    .exchange(tiingoSymbol.getExchangeCode())
                    .build();
        } catch (Exception e) {
//            throw new Exception500("심볼 조회 실패: " + e.getMessage());
            return new SymbolRequest.MoyaSymbol();
        }
    }

    private SymbolRequest.TiingoSymbol getTiingoSymbol(Response response, ObjectMapper om) throws IOException {
        if (response.isSuccessful()) {
            String json = response.body() != null ? response.body().string() : "";
            JsonNode rootNode = om.readTree(json);
            if (!rootNode.path("detail").isEmpty()) return new SymbolRequest.TiingoSymbol();
            return SymbolRequest.TiingoSymbol.builder()
                    .ticker(rootNode.path("ticker").asText())
                    .name(rootNode.path("name").asText(null))
                    .description(rootNode.path("description").asText(null))
                    .startDate(rootNode.path("startDate").asText(null))
                    .endDate(rootNode.path("endDate").asText(null))
                    .exchangeCode(rootNode.path("exchangeCode").asText(null))
                    .build();
        }
        throw new Exception400("symbol", "심볼을 찾을 수 없습니다");
    }
}
