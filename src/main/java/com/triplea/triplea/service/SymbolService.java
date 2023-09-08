package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SymbolService {
    @Value("${moya.token}")
    private String moyaToken;

    @Value("${tiingo.token}")
    private String tiingoToken;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public List<SymbolResponse.SymbolDTO> searchSymbol(String symbol){

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/stock")
                .queryParam("token", moyaToken)
                .queryParam("search", symbol);

        String url = builder.toUriString();

        ResponseEntity<ApiResponse.BookmarkSymbolDTO[]> response;
        try {
            response = restTemplate.getForEntity(url, ApiResponse.BookmarkSymbolDTO[].class);
        } catch (RestClientException e) {
            log.error(url, e.getMessage());
            throw new Exception500("API 호출 실패");
        }

        List<SymbolResponse.SymbolDTO> symbolDTOList = new ArrayList<>();
        ApiResponse.BookmarkSymbolDTO[] dtos = response.getBody();
        for(ApiResponse.BookmarkSymbolDTO dto : dtos){
            SymbolResponse.SymbolDTO symbolDTO = new SymbolResponse.SymbolDTO(dto);
            symbolDTOList.add(symbolDTO);
        }
        boolean flag = false;
        for(SymbolResponse.SymbolDTO dto : symbolDTOList){
            if (dto.getSymbol().equals(symbol)){
                flag = true;
            }
        }
        if (flag == false){

            String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
            tiingoUrl = tiingoUrl.replace("<ticker>", symbol);
            builder = UriComponentsBuilder.fromHttpUrl(tiingoUrl)
                    .queryParam("token", tiingoToken);

            url = builder.toUriString();

            ResponseEntity<ApiResponse.TiingoSymbol> tiingoresponse;
            try {
                tiingoresponse = restTemplate.getForEntity(url, ApiResponse.TiingoSymbol.class);
            } catch (RestClientException e) {
                log.error(url, e.getMessage());
                throw new Exception500("API 호출 실패");
            }



            ApiResponse.TiingoSymbol tiingoSymbol = tiingoresponse.getBody();

            SymbolResponse.SymbolDTO symbolDTO = new SymbolResponse.SymbolDTO(null,tiingoSymbol.getTicker(),tiingoSymbol.getName(),null,null,tiingoSymbol.getExchangeCode());
            symbolDTOList.add(symbolDTO);


        }

        return symbolDTOList;
    }

    public List<BookmarkResponse.BookmarkSymbolDTO> getSymbol(String symbol) {
        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

        String search = symbol;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/stock")
                .queryParam("token", moyaToken)
                .queryParam("search", search);

        String url = builder.toUriString();

        ResponseEntity<ApiResponse.BookmarkSymbolDTO[]> response;
        try {
            response = restTemplate.getForEntity(url, ApiResponse.BookmarkSymbolDTO[].class);
        } catch (RestClientException e) {
            log.error(url, e.getMessage());
            throw new Exception500("API 호출 실패");
        }

        ApiResponse.BookmarkSymbolDTO[] dtos = response.getBody();

        for (ApiResponse.BookmarkSymbolDTO dto : dtos) {

            if (dto == null)
                continue;

            //symbol 글자 완전 일치하는것만 가져온다
            if (dto.getSymbol().equals(search.toUpperCase())) {

                LocalDate today = LocalDate.now();
                LocalDate twoWeeksAgo = today.minusWeeks(2);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                //2주전 부터 오늘까지 데이터 가져오기
                String todayStr = today.format(formatter);
                String twoWeeksAgoStr = twoWeeksAgo.format(formatter);

                String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>/prices";
                tiingoUrl = tiingoUrl.replace("<ticker>", search);
                builder = UriComponentsBuilder.fromHttpUrl(tiingoUrl)
                        .queryParam("token", tiingoToken)
                        .queryParam("startDate", twoWeeksAgoStr)
                        .queryParam("endDate", todayStr)
                        .queryParam("sort", "-date");

                url = builder.toUriString();

                ResponseEntity<ApiResponse.Tiingo[]> tiingoresponse;

                try {
                    tiingoresponse = restTemplate.getForEntity(url, ApiResponse.Tiingo[].class);
                } catch (RestClientException e) {
                    log.error(url, e.getMessage());
                    throw new Exception500("API 호출 실패");
                }

                ApiResponse.Tiingo[] tiingoList = tiingoresponse.getBody();

                if (null == tiingoList)
                    continue;

                if (tiingoList.length < 2) {
                    throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
                }

                BookmarkResponse.Price price = BookmarkResponse.Price.builder()
                        .today(tiingoList[0])
                        .yesterday(tiingoList[1])
                        .build();

                String logo = dto.getLogo();
                if (logo == null) {
                    logo = LogoUtil.makeLogo(search);
                }
                String symbolcopy = dto.getSymbol();
                if (symbolcopy == null)
                    symbolcopy = symbol;

                String companyName = dto.getCompanyName();
                if (companyName == null) {
                    tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
                    tiingoUrl = tiingoUrl.replace("<ticker>", search);
                    builder = UriComponentsBuilder.fromHttpUrl(tiingoUrl)
                            .queryParam("token", tiingoToken);

                    url = builder.toUriString();

                    ResponseEntity<ApiResponse.TiingoSymbol> tiingoSymbolResponse;

                    try {
                        tiingoSymbolResponse = restTemplate.getForEntity(url, ApiResponse.TiingoSymbol.class);
                    } catch (RestClientException e) {
                        log.error(url, e.getMessage());
                        throw new Exception500("API 호출 실패");
                    }

                    if (null == tiingoSymbolResponse.getBody())
                        continue;

                    companyName = tiingoSymbolResponse.getBody().getName();
                }

                BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                        dto.getId(),
                        symbolcopy,
                        companyName,
                        dto.getSector(),
                        logo,
                        dto.getMarketType(),
                        price
                );

                bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
                break;
            }
        }

        return bookmarkSymbolDTOList;
    }
}
