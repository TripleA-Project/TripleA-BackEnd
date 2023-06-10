package com.triplea.triplea.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.category.CategoryRequest;
import com.triplea.triplea.dto.news.ApiResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class MoyaNewsProvider {
    private final OkHttpClient CLIENT = new OkHttpClient();
    private final MediaType MEDIATYPE = MediaType.parse("application/json");
    private final ObjectMapper OM;

    @Value("${moya.token}")
    private String token;

    @Value("${tiingo.token}")
    private String tiingoToken;

    /**
     * MoYa API 카테고리로 뉴스 ID 조회
     * @param category 대분류 카테고리
     * @return Response
     * @throws IOException execute
     */
    public Response getNewsIdByCategory(String category) throws IOException {
        CategoryRequest categoryRequest = new CategoryRequest(category);
        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/global-category").newBuilder();
        url.addQueryParameter("token", token);
        RequestBody requestBody = RequestBody.create(OM.writeValueAsString(categoryRequest), MEDIATYPE);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .post(requestBody)
                .header("accept", "*/*")
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * MoYa API 키워드로 뉴스 ID 조회
     * @param keyword 키워드
     * @return Response
     * @throws IOException execute
     */
    public Response getNewsIdByKeyword(String keyword) throws IOException {
        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/global-keyword").newBuilder();
        url.addQueryParameter("keyword", keyword);
        url.addQueryParameter("token", token);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * MoYa API 뉴스 ID만 List 로 return
     * @param getNewsId 뉴스 ID
     * @return 뉴스 ID List
     * @throws IOException json
     */
    public List<Long> getNewsId(Response getNewsId) throws IOException {
        List<Long> newsIds = new ArrayList<>();
        if (getNewsId.isSuccessful()) {
            if (getNewsId.body() != null) {
                JsonNode rootNode = OM.readTree(getNewsId.body().string());
                newsIds = StreamSupport.stream(rootNode.spliterator(), false)
                        .map(node -> node.path("id").asLong())
                        .collect(Collectors.toList());
            }
        }
        return newsIds;
    }

    /**
     * MoYa API 뉴스 ID로 뉴스 조회 Request
     * @param newsId 뉴스 ID
     * @return Response
     * @throws IOException execute
     */
    public Response getNewsById(Long newsId) throws IOException {
        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/globalnews").newBuilder();
        url.addQueryParameter("id", newsId.toString());
        url.addQueryParameter("token", token);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * MoYa API 뉴스 ID로 뉴스 조회 Response
     * @param getNewsById 뉴스 ID로 뉴스 조회한 Response
     * @return Details DTO
     * @throws IOException json
     */
    public ApiResponse.Details getNewsDetails(Response getNewsById) throws IOException {
        if (getNewsById.isSuccessful()) {
            if (getNewsById.body() != null) {
                JsonNode rootNode = OM.readTree(getNewsById.body().string());
                return ApiResponse.Details.builder()
                        .id(rootNode.path("id").asLong())
                        .symbol(rootNode.path("symbol").asText())
                        .source(rootNode.path("source").asText())
                        .title(rootNode.path("title").asText())
                        .description(rootNode.path("description").asText())
                        .summary(rootNode.path("summary").asText())
                        .thumbnail(rootNode.path("thumbnail").asText())
                        .url(rootNode.path("url").asText())
                        .publishedDate(rootNode.path("publishedDate").asText())
                        .content(rootNode.path("content").asText())
                        .category(rootNode.path("category").asText())
                        .keyword1(rootNode.path("keyword1").asText())
                        .keyword2(rootNode.path("keyword2").asText())
                        .keyword3(rootNode.path("keyword3").asText())
                        .sentiment(rootNode.path("sentiment").asInt())
                        .build();
            }
            throw new Exception400("newsId", "뉴스를 찾을 수 없습니다");
        }
        throw new Exception500("MoYa 뉴스 상세 조회 API 실패");
    }

    /**
     * MoYa API 로 symbol 검색
     * @param symbol symbol
     * @param getLogo logo 를 가져오려는 목적인지 true/false 로 확인
     * @return MoyaSymbol
     */
    public ApiResponse.MoyaSymbol getSymbol(String symbol, boolean getLogo) {
        HttpUrl.Builder url = HttpUrl.parse("https://api.moya.ai/stock").newBuilder();
        url.addQueryParameter("search", symbol);
        url.addQueryParameter("token", token);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty() && !json.equals("[]")) {
                    JsonNode rootNode = OM.readTree(json).get(0);
                    if (rootNode.path("symbol").asText().equals(symbol)) {
                        return ApiResponse.MoyaSymbol.builder()
                                .id(rootNode.path("id").asLong())
                                .symbol(rootNode.path("symbol").asText())
                                .companyName(rootNode.path("companyName").asText())
                                .exchange(rootNode.path("exchange").asText())
                                .industry(rootNode.path("industry").asText())
                                .website(rootNode.path("website").asText())
                                .description(rootNode.path("description").asText())
                                .CEO(rootNode.path("CEO").asText())
                                .issueType(rootNode.path("issueType").asText())
                                .sector(rootNode.path("sector").asText())
                                .logo(rootNode.path("logo").asText())
                                .marketType(rootNode.path("marketType").asText())
                                .build();
                    }
                }
//                throw new Exception400("symbol", "심볼을 찾을 수 없습니다");
                if (!getLogo) {
                    ApiResponse.TiingoSymbol tiingoSymbol = getAnotherSymbol(symbol);
                    return ApiResponse.MoyaSymbol.builder()
                            .symbol(tiingoSymbol.getTicker())
                            .companyName(tiingoSymbol.getName())
                            .description(tiingoSymbol.getDescription())
                            .exchange(tiingoSymbol.getExchangeCode())
                            .marketType(tiingoSymbol.getExchangeCode())
                            .build();
                }
            }
//            throw new Exception500("MoYa 심볼 조회 API 실패");
            return new ApiResponse.MoyaSymbol();
        } catch (Exception e) {
            throw new Exception500("심볼 조회 실패: " + e.getMessage());
        }
    }

    /**
     * Tiingo API 로 symbol(ticker) 조회
     * @param symbol symbol
     * @return TiingoSymbol
     */
    private ApiResponse.TiingoSymbol getAnotherSymbol(String symbol) {
        HttpUrl.Builder url = HttpUrl.parse("https://api.tiingo.com/tiingo/daily/" + symbol).newBuilder();
        url.addQueryParameter("token", tiingoToken);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                JsonNode rootNode = OM.readTree(json);
                if (rootNode.path("detail") == null) {
                    return ApiResponse.TiingoSymbol.builder()
                            .ticker(rootNode.path("ticker").asText())
                            .name(rootNode.path("name").asText())
                            .description(rootNode.path("description").asText())
                            .startDate(rootNode.path("startDate").asText())
                            .endDate(rootNode.path("endDate").asText())
                            .exchangeCode(rootNode.path("exchangeCode").asText())
                            .build();
                }
            }
            return new ApiResponse.TiingoSymbol();
        } catch (Exception e) {
            throw new Exception500("심볼 조회 실패: " + e.getMessage());
        }
    }
}
