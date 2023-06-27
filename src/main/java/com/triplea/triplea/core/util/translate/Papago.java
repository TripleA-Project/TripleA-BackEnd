package com.triplea.triplea.core.util.translate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.NewsRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Papago extends Translator {
    @Value("${translate.papago.client-id}")
    private String ClientId;
    @Value("${translate.papago.client-secret}")
    private String ClientSecret;

    public Papago(ObjectMapper OM) {
        super(OM);
    }

    @Override
    public String translate(String text) {
        if (text == null) return null;

        RequestBody requestBody;

        // 1회 5,000자까지만 번역이 가능
        if (text.length() > 5000) {
            StringBuilder result = new StringBuilder();
            String[] contents = text.split("(?<=\\. )");
            try {
                for (String content : contents) {
                    requestBody = RequestBody.create(
                            OM.writeValueAsString(NewsRequest.TranslateIn.Papago.builder()
                                    .text(content)
                                    .build()), MEDIATYPE);
                    Response response = getResponse(requestBody);
                    result.append(getJson(response)).append(" ");
                }
                return result.toString().trim();
            } catch (Exception e) {
                return null;
            }
        }

        try {
            requestBody = RequestBody.create(
                    OM.writeValueAsString(NewsRequest.TranslateIn.Papago.builder()
                            .text(text)
                            .build()), MEDIATYPE);
        } catch (Exception e) {
            log.error("Papago 번역 요청 실패: " + e.getMessage());
            return null;
        }
        try (Response response = getResponse(requestBody)) {
            return getJson(response);
        } catch (Exception e) {
            log.error("Papago 번역 실패: " + e.getMessage());
            return null;
        }
    }

    private Response getResponse(RequestBody requestBody) throws IOException {
        Request request = new Request.Builder().url("https://naveropenapi.apigw.ntruss.com/nmt/v1/translation")
                .post(requestBody)
                .headers(Headers.of("accept", MEDIATYPE.type(), "content-type", MEDIATYPE.type(), "X-NCP-APIGW-API-KEY-ID", ClientId, "X-NCP-APIGW-API-KEY", ClientSecret))
                .build();
        return CLIENT.newCall(request).execute();
    }

    private String getJson(Response response) throws IOException {
        if (response.isSuccessful()) {
            String json = response.body() != null ? response.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                JsonNode translate = rootNode.path("message").path("result").path("translatedText");
                return translate.asText();
            }
        }
        throw new Exception500("Response 실패");
    }
}