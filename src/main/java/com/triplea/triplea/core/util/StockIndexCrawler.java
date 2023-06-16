package com.triplea.triplea.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.stock.StockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockIndexCrawler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper OM;

    @Scheduled(cron = "0 20 17 * * *", zone = "America/New_York") // 매일 오후 5시 20분에 실행 (EST 기준)
    public void getStockIndex() {
        final String IXIC_URL = "https://finance.yahoo.com/quote/^IXIC?p=^IXIC&.tsrc=fin-srch";
        final String DJI_URL = "https://finance.yahoo.com/quote/^DJI?p=^DJI&.tsrc=fin-srch";
        final String GSPC_URL = "https://finance.yahoo.com/quote/^GSPC?p=^GSPC&.tsrc=fin-srch";
        final String urlSelector = "#quote-header-info > div:nth-child(3) > div:nth-child(1) > div";
        StockResponse.Index.Stock nasdaq = null;
        StockResponse.Index.Stock dowJones = null;
        StockResponse.Index.Stock sp500 = null;
        try {
            Document IXICdocument = Jsoup.connect(IXIC_URL).get();
            Document DJIdocument = Jsoup.connect(DJI_URL).get();
            Document GSPCdocument = Jsoup.connect(GSPC_URL).get();

            nasdaq = parseStockElement(IXICdocument, urlSelector);
            dowJones = parseStockElement(DJIdocument, urlSelector);
            sp500 = parseStockElement(GSPCdocument, urlSelector);
            log.debug("웹 크롤링 성공");
        } catch (IOException e) {
            log.error("웹 크롤링 실패: " + e.getMessage());
            getStockIndex();
        }
        saveRedis(nasdaq, dowJones, sp500);
    }

    public StockResponse.Index.Stock parseStockElement(Document document, String urlSelector) {
        Element element = document.selectFirst(urlSelector);
        String nameSelector = "#quote-header-info > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > h1";
        Element nameElement = document.selectFirst(nameSelector);

        String priceSelector = "fin-streamer:nth-child(1)";
        String percentSelector = "fin-streamer:nth-child(3) > span";
        String todaySelector = "fin-streamer:nth-child(2) > span";

        String fullName = nameElement.text();
        String[] names = fullName.split("[(]");
        String name = names[0].trim();
        String symbol = names[1].replaceAll("[)]","");
        String price = element.selectFirst(priceSelector).text();
        String percent = element.selectFirst(percentSelector).text().replaceAll("[()]", "");
        String today = element.selectFirst(todaySelector).text();

        return StockResponse.Index.Stock.builder()
                .symbol(symbol)
                .name(name)
                .price(price)
                .percent(percent)
                .today(today)
                .build();
    }

    @Transactional
    public void saveRedis(StockResponse.Index.Stock nasdaq, StockResponse.Index.Stock dowJones, StockResponse.Index.Stock sp500){
        String nasdaqSerialize;
        String dowJonesSerialize;
        String sp500Serialize;
        try {
            nasdaqSerialize = OM.writeValueAsString(nasdaq);
            dowJonesSerialize = OM.writeValueAsString(dowJones);
            sp500Serialize = OM.writeValueAsString(sp500);
        } catch (Exception e) {
            throw new Exception500("serialize 실패: " + e.getMessage());
        }

        redisTemplate.opsForValue().set("index_ixic",nasdaqSerialize);
        redisTemplate.opsForValue().set("index_dji",dowJonesSerialize);
        redisTemplate.opsForValue().set("index_gspc",sp500Serialize);
    }
}
