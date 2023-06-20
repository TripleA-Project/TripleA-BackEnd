package com.triplea.triplea.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockIndexCrawler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper OM;
    private final List<String> indexList = List.of("^IXIC", "^DJI", "^GSPC");

    @Scheduled(cron = "0 20 17 * * *", zone = "America/New_York") // 매일 오후 5시 20분에 실행 (EST 기준)
    public void getStockIndex() {
        String URL = "https://finance.yahoo.com/quote/";
        List<StockResponse.Index.Stock> results = new ArrayList<>();
        try {
            for (String index : indexList) {
                Document document = Jsoup.connect(URL + index).get();
                results.add(parseStockElement(document));
            }
            log.debug("웹 크롤링 성공");
        } catch (IOException e) {
            log.error("웹 크롤링 실패: " + e.getMessage());
            getStockIndex();
        }
        saveRedis(results);
    }

    public StockResponse.Index.Stock parseStockElement(Document document) {
        String urlSelector = "#quote-header-info > div:nth-child(3) > div:nth-child(1) > div";
        String nameSelector = "#quote-header-info > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > h1";
        Element element = document.selectFirst(urlSelector);
        Element nameElement = document.selectFirst(nameSelector);

        String priceSelector = "fin-streamer:nth-child(1)";
        String percentSelector = "fin-streamer:nth-child(3) > span";
        String todaySelector = "fin-streamer:nth-child(2) > span";

        String fullName = nameElement.text();
        String[] names = fullName.split("[(]");
        String name = names[0].trim();
        String symbol = names[1].replaceAll("[)]", "");
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
    public void saveRedis(List<StockResponse.Index.Stock> indexList) {
        try {
            for (StockResponse.Index.Stock index : indexList) {
                String serialize = OM.writeValueAsString(index);
                redisTemplate.opsForValue().set("index_" + index.getSymbol(), serialize);
            }
        } catch (Exception e) {
            log.error("redis 저장 실패: " + e.getMessage());
        }
    }
}
