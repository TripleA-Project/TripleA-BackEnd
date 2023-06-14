package com.triplea.triplea.core.util.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.NewsRequest;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        if(text == null) return null;

        RequestBody requestBody;
        try {
            requestBody = RequestBody.create(
                    OM.writeValueAsString(NewsRequest.TranslateIn.Papago.builder()
                            .text(text)
                            .build()), MEDIATYPE);
        } catch (JsonProcessingException e) {
            throw new Exception500("Papago 번역 요청 실패: " + e.getMessage());
        }
        Request request = new Request.Builder().url("https://naveropenapi.apigw.ntruss.com/nmt/v1/translation")
                .post(requestBody)
                .headers(Headers.of("accept", MEDIATYPE.type(), "content-type", MEDIATYPE.type(), "X-NCP-APIGW-API-KEY-ID", ClientId, "X-NCP-APIGW-API-KEY", ClientSecret))
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty()) {
                    JsonNode rootNode = OM.readTree(json);
                    JsonNode translate = rootNode.path("message").path("result").path("translatedText");
                    return translate.asText();
                }
                throw new Exception500("Response 실패");
            }
        } catch (Exception e) {
            throw new Exception500("Papago 번역 실패: " + e.getMessage());
        }
        throw new Exception500("Papago 번역 실패");
    }
}
