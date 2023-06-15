package com.triplea.triplea.core.util.translate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public abstract class Translator {
   final OkHttpClient CLIENT = new OkHttpClient();
    final MediaType MEDIATYPE = MediaType.parse("application/json");
    final ObjectMapper OM;
    public abstract String translate(String text);
}
