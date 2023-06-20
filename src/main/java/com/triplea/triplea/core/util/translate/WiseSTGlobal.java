package com.triplea.triplea.core.util.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception404;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsRequest;
import com.triplea.triplea.dto.news.NewsResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class WiseSTGlobal extends Translator {
    @Value("${translate.token}")
    private String TOKEN;

    @Value("${translate.openai-api-key}")
    private String OPENAI_API_KEY;

    public WiseSTGlobal(ObjectMapper OM) {
        super(OM);
    }

    @Override
    public String translate(String text) {
        if(text == null) return null;

        String[] contents = text.split("(?<=\\. )");
        RequestBody requestBody;
        try {
            requestBody = RequestBody.create(OM.writeValueAsString(NewsRequest.TranslateIn.WiseSTGlobal.builder().contents(contents).build()), MEDIATYPE);
        } catch (JsonProcessingException e) {
            throw new Exception500("WiseSTGlobal 번역 요청 실패: " + e.getMessage());
        }
        Request request = new Request.Builder().url("https://wisetranslate.net/api/translate/ai")
                .post(requestBody)
                .headers(Headers.of("accept", MEDIATYPE.type(), "content-type", MEDIATYPE.type(), "Authorization", TOKEN))
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty()) {
                    JsonNode rootNode = OM.readTree(json);
                    return StreamSupport.stream(rootNode.path("translation").spliterator(), false)
                                    .map(JsonNode::asText)
                                    .collect(Collectors.joining(" "))
                                    .trim();
                }
                throw new Exception500("Response 실패");
            }
        } catch (Exception e) {
            throw new Exception500("WiseSTGlobal 번역 실패: " + e.getMessage());
        }
        throw new Exception500("WiseSTGlobal 번역 실패");
    }

    public NewsResponse.TranslateOut translateArticle(ApiResponse.Details text){
        if(text == null) return null;

        String[] contents = {text.getTitle(), text.getDescription(), text.getSummary(), text.getContent()};
        RequestBody requestBody;
        try {
            requestBody = RequestBody.create(OM.writeValueAsString(NewsRequest.TranslateIn.WiseSTGlobal.builder().contents(contents).build()), MEDIATYPE);
        } catch (JsonProcessingException e) {
            throw new Exception500("WiseSTGlobal 번역 요청 실패: " + e.getMessage());
        }
        Request request = new Request.Builder().url("https://wisetranslate.net/api/translate/ai")
                .post(requestBody)
                .headers(Headers.of("accept", MEDIATYPE.type(), "content-type", MEDIATYPE.type(), "Authorization", TOKEN))
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty()) {
                    JsonNode rootNode = OM.readTree(json);
                    JsonNode node = rootNode.path("translation");
                    return NewsResponse.TranslateOut.builder()
                            .title(node.get(0).asText(null))
                            .description(node.get(1).asText(null))
                            .summary(node.get(2).asText(null))
                            .content(node.get(3).asText(null))
                            .build();
                }
                throw new Exception500("Response 실패");
            }
        } catch (Exception e) {
            throw new Exception500("WiseSTGlobal 번역 실패: " + e.getMessage());
        }
        throw new Exception500("WiseSTGlobal 번역 실패");
    }

    public Response analysis(Long newsId, String summary) throws IOException {
        HttpUrl.Builder url = HttpUrl.parse("https://wisetranslate.net/api/gnews/analyze").newBuilder();
        url.addQueryParameter("openai_api_key",OPENAI_API_KEY);
        url.addQueryParameter("id", newsId.toString());
        if(summary != null ) url.addQueryParameter("article", summary);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .headers(Headers.of("accept", MEDIATYPE.toString(), "Authorization", TOKEN))
                .build();

        return CLIENT.newCall(request).execute();
    }

    public NewsResponse.Analysis getAnalysis(Response response) throws IOException {
        if (response.isSuccessful()) {
            String json = response.body() != null ? response.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                return NewsResponse.Analysis.builder()
                        .impact(rootNode.path("impact").asText())
                        .action(rootNode.path("action").asText())
                        .comment(rootNode.path("comment").asText())
                        .model(rootNode.path("model").asText())
                        .build();
            }
            throw new Exception500("Response 실패");
        } else if (response.code() == 404) {
            throw new Exception404("News를 찾을 수 없습니다");
        }
        throw new Exception500("AI 분석 실패");
    }
}
