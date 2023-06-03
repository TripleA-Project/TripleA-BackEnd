package com.triplea.triplea.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.NewsRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class Translator {
    private final OkHttpClient CLIENT = new OkHttpClient();
    private final MediaType MEDIATYPE = MediaType.parse("application/json");
    private final ObjectMapper OM;
    @Value("${translate.token}")
    private String TOKEN;

    public String translate(String content) throws IOException {
        String[] contents = content.split("(?<=\\. )");
        RequestBody requestBody = RequestBody.create(OM.writeValueAsString(NewsRequest.TranslateIn.builder().contents(contents).build()), MEDIATYPE);
        Request request = new Request.Builder().url("https://wisetranslate.net/api/translate/ai")
                .post(requestBody)
                .headers(Headers.of("accept", MEDIATYPE.type(), "content-type", MEDIATYPE.type(), "Authorization", TOKEN))
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                if (!json.isEmpty()) {
                    StringJoiner result = new StringJoiner(" ");
                    JsonNode rootNode = OM.readTree(json);
                    for (JsonNode translation : rootNode.path("translation")) result.add(translation.asText());
                    return result.toString().trim();
                }
                throw new Exception500("translate Response 실패");
            }
            throw new Exception500("translate 실패");
        }
    }
}
