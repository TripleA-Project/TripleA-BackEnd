package com.triplea.triplea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.core.util.StockIndexCrawler;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper OM;

    @Value("${tiingo.token}")
    private String tiingoToken;

    @Value("${moya.token}")
    private String moyaToken;

    private final StepPaySubscriber subscriber;
    private final RestTemplate restTemplate = new RestTemplate();

    private final CustomerRepository customerRepository;

    // 주가 지수 조회
    public StockResponse.Index getStockIndex() {
        List<StockResponse.Index.Stock> indexes = new ArrayList<>();
        try {
            for(String index : StockIndexCrawler.INDEXES){
                String serialize = redisTemplate.opsForValue().get("index_" + index);
                indexes.add(OM.readValue(serialize, StockResponse.Index.Stock.class));
            }
        } catch (Exception e) {
            throw new Exception500("주가 지수 조회 실패: " + e.getMessage());
        }
        return new StockResponse.Index(indexes);
    }

    @Transactional(readOnly = true)
    public StockResponse.StockInfoDTO getChart(String symbol, String startDate, String endDate, String resampleFreq, User user){


        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (start.isAfter(end)) {
            throw new Exception400("date", "Start date must be before or equal to end date");
        }

        User.Membership membership = User.Membership.BASIC;

        Optional<Customer> opCustomer = customerRepository.findCustomerByUserId(user.getId());
        if(opCustomer.isPresent()){

            boolean isSubscribe = false;
            try {
                isSubscribe = subscriber.isSubscribe(opCustomer.get().getSubscriptionId());
            } catch (Exception e) {
                throw new Exception500("구독 확인 실패: " + e.getMessage());
            }

            if(isSubscribe){
                membership = User.Membership.PREMIUM;
            }
        }

        String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>/prices";
        tiingoUrl = tiingoUrl.replace("<ticker>", symbol);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tiingoUrl)
                .queryParam("token", tiingoToken)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .queryParam("resampleFreq", resampleFreq);//daily, weekly, monthly, annually

        String url = builder.toUriString();

        ResponseEntity<ApiResponse.Tiingo[]> tiingoresponse;

        try {
            tiingoresponse = restTemplate.getForEntity(url, ApiResponse.Tiingo[].class);
        } catch (RestClientException e) {
            log.error(url, e.getMessage());
            throw new Exception500("API 호출 실패");
        }

        ApiResponse.Tiingo[] arrTiingo = tiingoresponse.getBody();
        if (arrTiingo.length == 0) {
            throw new Exception500("API 결과가 비어 있습니다.");
        }

        //오늘 시간이 AM6:30 이전 이라면
        //엊그제 넘어서면 엊그제로
        //오늘 AM6:30 이후시간 이라면
        //어제 넘어서면 어제로 시간 만들기
        ZoneId koreaTime = ZoneId.of("Asia/Seoul");
        LocalDateTime nowDateTime = LocalDateTime.now(koreaTime);
        LocalDateTime timeBoundary = nowDateTime.withHour(6).withMinute(30).withSecond(0).withNano(0);

        LocalDate targetDate;//tiingo 가장 최신 데이터 날짜
        if(nowDateTime.isBefore(timeBoundary)){
            // 현재 시간이 오전 6:30 이전이라면, 엊그제 날짜를 구합니다.
            targetDate = nowDateTime.toLocalDate().minusDays(2);
        }else{
            // 현재 시간이 오전 6:30 이후라면, 어제 날짜를 구합니다.
            targetDate = nowDateTime.toLocalDate().minusDays(1);
        }

        List<ApiResponse.Tiingo> filteredList = new ArrayList<>();
        String formattedToday = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00.000Z";
        for (ApiResponse.Tiingo tiingo : arrTiingo) {
            LocalDate tiingoDate = LocalDate.parse(tiingo.getFormattedDate());
            if (tiingoDate.isAfter(targetDate)) {
                //nothing
            }else{
                filteredList.add(tiingo);
            }
        }

        String firstDate = arrTiingo[0].getFormattedDate();
        String lastDate = (arrTiingo.length == 1) ? firstDate : arrTiingo[arrTiingo.length - 1].getFormattedDate();
        // 마지막 날짜에 하루 추가. Moya 는 내일거를 넣어야 오늘거를 받음
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        lastDate = LocalDate.parse(lastDate, formatter).plusDays(1).toString();

        String moyaURL = "https://api.moya.ai/globalbuzzduration";
        builder = UriComponentsBuilder.fromHttpUrl(moyaURL)
                .queryParam("symbol", symbol)
                .queryParam("token", moyaToken)
                .queryParam("startDate", firstDate)
                .queryParam("endDate", lastDate);

        url = builder.toUriString();

        ResponseEntity<StockResponse.GlobalBuzzDuration> moyaresponse;

        try {
            moyaresponse = restTemplate.getForEntity(url, StockResponse.GlobalBuzzDuration.class);
        } catch (RestClientException e) {
            log.error(url, e.getMessage());
            throw new Exception500("API 호출 실패");
        }

        StockResponse.GlobalBuzzDuration globalBuzzDuration = moyaresponse.getBody();

        List<StockResponse.Chart> charts = new ArrayList<>();

        for(ApiResponse.Tiingo tiingo : filteredList){
            StockResponse.Chart chart = new StockResponse.Chart(tiingo);
            for (StockResponse.BuzzData buzzData : globalBuzzDuration.getBuzzDatas()) {
                if (tiingo.getFormattedDate().equals(buzzData.getPublishedDate())) {
//                  Tiingo와 BuzzData의 날짜가 일치하면 Chart 객체 생성
                    chart.setSentiment(buzzData.getSentiment()); // sentiment 설정
                    chart.setBuzz(buzzData.getCount()); // buzz 설정
                    break;
                }
            }

            charts.add(chart); // 생성한 Chart 객체를 charts 리스트에 추가
        }

        String companyName = "";
        List<StockResponse.CompanyInfo> companyInfoList = globalBuzzDuration.getCompanyInfo();
        if(companyInfoList != null && companyInfoList.isEmpty()==false)
            companyName = companyInfoList.get(0).getCompanyName();

        StockResponse.StockInfoDTO stockInfoDTO = new StockResponse.StockInfoDTO(membership.toString(), symbol, companyName, charts);



        return stockInfoDTO;
    }

    public void changeChart(List<StockResponse.Chart> charts){
        for(StockResponse.Chart chart : charts){
            chart.getDate();
        }
    }

    public StockResponse.StockInfoDTO changeBuzz(StockResponse.StockInfoDTO stockInfoDTO, String resampleFreq, User user) {
        if (resampleFreq.equals("weekly")) {
            for (StockResponse.Chart chart : stockInfoDTO.getCharts()) {
                String chartDate = chart.getDate();
                LocalDateTime dateTime = LocalDateTime.parse(chartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                LocalDate date = dateTime.toLocalDate();
                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate startDate = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                DayOfWeek dayOfWeek = startDate.getDayOfWeek();
                String mondayStr = String.valueOf(startDate);

                if (dayOfWeek == DayOfWeek.MONDAY) {
                    //nothing
                } else {
                    LocalDate monday = startDate.minusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());
                    LocalDateTime mondayDateTime = monday.atStartOfDay();
                    mondayStr = mondayDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                StockResponse.StockInfoDTO changeBuzz = getChart(stockInfoDTO.getSymbol(), mondayStr, formattedDate, "daily", user);
                int buzz = 0;
                for (int i = 0; i < changeBuzz.getCharts().size(); i++) {
                    buzz += changeBuzz.getCharts().get(i).getBuzz();
                }
                chart.setBuzz(buzz);
            }
        }else if (resampleFreq.equals("monthly")){
            for (StockResponse.Chart chart : stockInfoDTO.getCharts()) {
                String chartDate = chart.getDate();
                LocalDateTime dateTime = LocalDateTime.parse(chartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));


                int year = dateTime.getYear();
                int month = dateTime.getMonthValue();
                String startDate = String.format("%d-%02d-01", year, month); // 월을 2자리 숫자로 포맷



                String formattedDateStr = chartDate.replace("T", " ").replace("Z", "");
                String[] parts = formattedDateStr.split(" ");
                String result = parts[0];
                StockResponse.StockInfoDTO changeBuzz = getChart(stockInfoDTO.getSymbol(), startDate, result, "daily", user);
                int buzz = 0;
                for (int i = 0; i < changeBuzz.getCharts().size(); i++) {
                    buzz += changeBuzz.getCharts().get(i).getBuzz();
                }
                chart.setBuzz(buzz);
            }
        }

        return stockInfoDTO;
    }


}
