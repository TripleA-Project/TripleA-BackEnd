package com.triplea.triplea.core.util.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsRequest;
import com.triplea.triplea.dto.news.NewsResponse;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class WiseSTGlobal extends Translator {
    @Value("${translate.token}")
    private String TOKEN;

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
}
