package com.triplea.triplea.core.util.provide.symbol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public abstract class SymbolProvider {
    public final OkHttpClient CLIENT = new OkHttpClient();
    public final ObjectMapper OM = new ObjectMapper();
    protected abstract HttpUrl.Builder getUrl(String symbol);

    public Response getSymbol(String symbol) throws IOException {
        HttpUrl.Builder url = getUrl(symbol);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();
        return CLIENT.newCall(request).execute();
    }
    public abstract SymbolRequest.MoyaSymbol getSymbolInfo(String symbol);

    /**
     * logo 가 없으면 대체하기 위한 메소드
     * @param symbol symbol
     * @return String logo
     */
    public String getLogo(SymbolRequest.MoyaSymbol symbol) {
        String logo = symbol.getLogo();
        if (logo == null || logo.equals("null"))
            logo = "https://storage.googleapis.com/iex/api/logos/" + symbol.getSymbol() + ".png";
        return logo;
    }
}
