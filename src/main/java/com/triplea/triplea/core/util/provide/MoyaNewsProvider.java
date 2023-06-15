package com.triplea.triplea.core.util.provide;

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
            String json = getNewsById.body() != null ? getNewsById.body().string() : "";
            JsonNode rootNode = OM.readTree(json);
            if (!json.isEmpty() && !json.equals("[]")) {
                return ApiResponse.Details.builder()
                        .id(rootNode.path("id").asLong())
                        .symbol(rootNode.path("symbol").asText())
                        .source(rootNode.path("source").asText())
                        .title(rootNode.path("title").asText())
                        .description(rootNode.path("description").asText(null))
                        .summary(rootNode.path("summary").asText(null))
                        .thumbnail(rootNode.path("thumbnail").asText())
                        .url(rootNode.path("url").asText())
                        .publishedDate(rootNode.path("publishedDate").asText())
                        .content(rootNode.path("content").asText(null))
                        .category(rootNode.path("category").asText(null))
                        .keyword1(rootNode.path("keyword1").asText(null))
                        .keyword2(rootNode.path("keyword2").asText(null))
                        .keyword3(rootNode.path("keyword3").asText(null))
                        .sentiment(rootNode.path("sentiment").asInt())
                        .build();
            }
            throw new Exception400("newsId", "뉴스를 찾을 수 없습니다");
        }
        throw new Exception500("MoYa 뉴스 상세 조회 API 실패");
    }
}
