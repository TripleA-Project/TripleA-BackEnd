package com.triplea.triplea.core.util.provide;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.stock.StockRequest;
import com.triplea.triplea.dto.stock.StockResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
@RequiredArgsConstructor
public class TiingoStockProvider {
    private final OkHttpClient CLIENT = new OkHttpClient();
    private final ObjectMapper OM;

    @Value("${tiingo.token}")
    private String token;

    /**
     * Tiingo API 로 symbol(ticker)의 해당 날짜 주가 조회
     *
     * @param symbol    symbol
     * @param startDate yesterday
     * @param endDate   today
     * @return SymbolPrice
     */
    public StockResponse.Price getStocks(String symbol, LocalDate startDate, LocalDate endDate) {
        // startDate와 endDate 값이 바뀌었을 경우 실행
        if (startDate.isAfter(endDate)) {
            LocalDate date = startDate;
            startDate = endDate;
            endDate = date;
        }

        // 주말의 경우 주가 제공이 되지 않으므로 주말인지 검증해서 주말이면 평일로 변경
//        if (isWeekend(startDate) || isWeekend(endDate)) {
//            startDate = adjustWeekendToWeekday(startDate, true);
//            endDate = adjustWeekendToWeekday(endDate, false);
//        }

        HttpUrl.Builder url = HttpUrl.parse("https://api.tiingo.com/tiingo/daily/" + symbol + "/prices").newBuilder();
        url.addQueryParameter("startDate", startDate.toString());
        url.addQueryParameter("endDate", endDate.toString());
        url.addQueryParameter("sort", "-date");
        url.addQueryParameter("token", token);
        Request request = new Request.Builder()
                .url(url.build().toString())
                .get()
                .header("accept", "*/*")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : "";
                JsonNode rootNode = OM.readTree(json);

                // notFound 등 예외가 발생했을 때 status는 200인데 message로만 표시되는 경우가 있어서 예외 처리
                if (!rootNode.path("detail").isEmpty()) throw new Exception500("실패");

                JsonNode yesterday = rootNode.get(1);
                JsonNode today = rootNode.get(0);
                StockRequest.TiingoStock tiingoYesterday = dataStockPrice(yesterday);
                StockRequest.TiingoStock tiingoToday = dataStockPrice(today);
                return StockResponse.Price.builder()
                        .today(tiingoToday)
                        .yesterday(tiingoYesterday)
                        .build();
            }
            return new StockResponse.Price();
        } catch (Exception e) {
//            throw new Exception500("주가 조회 실패: " + e.getMessage());
            // 주가 조회 실패해도 나머지 데이터는 출력해야 하기 때문에 기본 DTO로 반환
            return new StockResponse.Price();
        }
    }

    private StockRequest.TiingoStock dataStockPrice(JsonNode node) {
        return StockRequest.TiingoStock.builder()
                .date(node.path("date").asText())
                .open(node.path("open").asDouble())
                .high(node.path("high").asDouble())
                .low(node.path("low").asDouble())
                .close(node.path("close").asDouble())
                .volume(node.path("volume").asLong())
                .adjOpen(node.path("adjOpen").asDouble())
                .adjHigh(node.path("adjHigh").asDouble())
                .adjLow(node.path("adjLow").asDouble())
                .adjClose(node.path("adjClose").asDouble())
                .adjVolume(node.path("adjVolume").asLong())
                .divCash(node.path("divCash").asDouble())
                .splitFactor(node.path("splitFactor").asDouble())
                .build();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.MONDAY;
    }

    private LocalDate adjustWeekendToWeekday(LocalDate date, boolean isStartDate) {
        if (!isWeekend(date)) return date;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (isStartDate) {
            // 시작날짜의 경우 금,토,일이면 목요일로
            if (dayOfWeek == DayOfWeek.FRIDAY) return date.minusDays(1);
            if (dayOfWeek == DayOfWeek.SATURDAY) return date.minusDays(2);
            if (dayOfWeek == DayOfWeek.SUNDAY) return date.minusDays(3);
        } else {
            // 끝날짜의 경우 토,일,월이면 금요일로
            if (dayOfWeek == DayOfWeek.SATURDAY) return date.minusDays(1);
            if (dayOfWeek == DayOfWeek.SUNDAY) return date.minusDays(2);
            if (dayOfWeek == DayOfWeek.MONDAY) return date.minusDays(3);
        }
        return date;
    }
}
