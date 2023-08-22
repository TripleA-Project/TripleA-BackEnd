package com.triplea.triplea.core.util.provide.symbol;

import com.fasterxml.jackson.databind.JsonNode;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class MoyaSymbolProvider extends SymbolProvider {
    @Value("${moya.token}")
    private String token;

    @Override
    protected HttpUrl.Builder getUrl(String symbol) {
        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/stock").newBuilder();
        url.addQueryParameter("search", symbol);
        url.addQueryParameter("token", token);
        return url;
    }

    /**
     * MoYa API 로 symbol 검색
     * @param symbol symbol
     * @return MoyaSymbol
     */
    @Override
    public SymbolRequest.MoyaSymbol getSymbolInfo(String symbol) {
        try (Response response = getSymbol(symbol)) {
//            throw new Exception500("MoYa 심볼 조회 API 실패");
            return getMoyaSymbol(response, symbol);
        } catch (Exception e) {
//            throw new Exception500("심볼 조회 실패: " + e.getMessage());
            // 심볼 조회 실패해도 나머지 데이터는 출력해야 하기 때문에 기본 DTO로 반환
            return new SymbolRequest.MoyaSymbol();
        }
    }

    private SymbolRequest.MoyaSymbol getMoyaSymbol(Response response, String symbol) throws IOException {
        if (response.isSuccessful()) {
            String json = response.body() != null ? response.body().string() : "";
            if (!json.isEmpty() && !json.equals("[]")) {
                JsonNode rootNode = OM.readTree(json).get(0);
                if (rootNode.path("symbol").asText().equals(symbol)) {
                    return SymbolRequest.MoyaSymbol.builder()
                            .id(rootNode.path("id").asLong())
                            .symbol(rootNode.path("symbol").asText())
                            .companyName(rootNode.path("companyName").asText(null))
                            .exchange(rootNode.path("exchange").asText(null))
                            .industry(rootNode.path("industry").asText(null))
                            .website(rootNode.path("website").asText(null))
                            .description(rootNode.path("description").asText(null))
                            .CEO(rootNode.path("CEO").asText(null))
                            .issueType(rootNode.path("issueType").asText(null))
                            .sector(rootNode.path("sector").asText(null))
                            .logo(rootNode.path("logo").asText(null))
                            .marketType(rootNode.path("marketType").asText(null))
                            .build();
                }
            }else return null;
//            throw new Exception400("symbol", "심볼을 찾을 수 없습니다");
        }
        throw new Exception500("MoYa 심볼 조회 API 실패");
    }

    public SymbolRequest.BuzzDuration getBuzz(String symbol, LocalDate startDate, LocalDate endDate) {
        // startDate와 endDate 값이 바뀌었을 경우 실행
        if (startDate.isAfter(endDate)) {
            LocalDate date = startDate;
            startDate = endDate;
            endDate = date;
        }

        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/globalbuzzduration").newBuilder();
        url.addQueryParameter("symbol", symbol);
        url.addQueryParameter("token", token);
        url.addQueryParameter("startDate", startDate.toString());
        url.addQueryParameter("endDate", endDate.toString());
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty() && !json.equals("[]")) {
                    JsonNode rootNode = OM.readTree(json);
                    JsonNode buzzDatas = rootNode.path("buzzDatas");
                    JsonNode avgSentiment = rootNode.path("avgSentiment");
                    JsonNode companyInfo = rootNode.path("companyInfo");
                    SymbolRequest.BuzzDuration.BuzzDatas datas = SymbolRequest.BuzzDuration.BuzzDatas.builder()
                            .sentiment(buzzDatas.path("sentiment").asDouble())
                            .count(buzzDatas.path("count").asInt())
                            .positiveCount(buzzDatas.path("positiveCount").asInt())
                            .negativeCount(buzzDatas.path("negativeCount").asInt())
                            .publishedDate(buzzDatas.path("publishedDate").asText())
                            .build();
                    SymbolRequest.BuzzDuration.AvgSentiment avg = SymbolRequest.BuzzDuration.AvgSentiment.builder()
                            .sentiment(avgSentiment.path("sentiment").asDouble())
                            .count(avgSentiment.path("count").asInt())
                            .positiveCount(avgSentiment.path("positiveCount").asInt())
                            .negativeCount(avgSentiment.path("negativeCount").asInt())
                            .build();
                    SymbolRequest.BuzzDuration.CompanyInfo company = SymbolRequest.BuzzDuration.CompanyInfo.builder()
                            .companyName(companyInfo.path("companyName").asText())
                            .symbol(companyInfo.path("symbol").asText())
                            .build();
                    return SymbolRequest.BuzzDuration.builder()
                            .buzzDatas(datas)
                            .avgSentiment(avg)
                            .companyInfo(company)
                            .build();
                }
            }
            return new SymbolRequest.BuzzDuration();
        } catch (Exception e) {
//            throw new Exception500("Buzz 조회 실패: " + e.getMessage());
            // Buzz 조회 실패해도 나머지 데이터는 출력해야 하기 때문에 기본 DTO로 반환
            return new SymbolRequest.BuzzDuration();
        }
    }
}
