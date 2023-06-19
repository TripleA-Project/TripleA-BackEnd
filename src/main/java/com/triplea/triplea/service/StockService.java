package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockService {

    @Value("${tiingo.token}")
    private String tiingoToken;

    @Value("${moya.token}")
    private String moyaToken;

    private final StepPaySubscriber subscriber;
    private final RestTemplate restTemplate = new RestTemplate();

    private final CustomerRepository customerRepository;

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
                .queryParam("resampleFreq", resampleFreq);

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

        String firstDate = arrTiingo[0].getFormattedDate();
        String lastDate = (arrTiingo.length == 1) ? firstDate : arrTiingo[arrTiingo.length - 1].getFormattedDate();
        // 마지막 날짜에 하루 추가
        lastDate = LocalDate.parse(lastDate).plusDays(1).toString();

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
        for(ApiResponse.Tiingo tiingo : arrTiingo){
            for (StockResponse.BuzzData buzzData : globalBuzzDuration.getBuzzDatas()) {
                if (tiingo.getFormattedDate().equals(buzzData.getPublishedDate())) {
                    // Tiingo와 BuzzData의 날짜가 일치하면 Chart 객체 생성
                    StockResponse.Chart chart = new StockResponse.Chart(tiingo);
                    chart.setSentiment(buzzData.getSentiment()); // sentiment 설정
                    chart.setBuzz(buzzData.getCount()); // buzz 설정
                    charts.add(chart); // 생성한 Chart 객체를 charts 리스트에 추가
                }
            }
        }

        StockResponse.StockInfoDTO stockInfoDTO = new StockResponse.StockInfoDTO(membership.toString(), symbol, globalBuzzDuration.getCompanyInfo().getCompanyName(), charts);

        return stockInfoDTO;
    }
}
