package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.symbol.SymbolRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkSymbolService {

    private final BookmarkSymbolRepository bookmarkSymbolRepository;

    private final SymbolRepository symbolRepository;

    @Value("${moya.token}")
    private String moyaToken;

    @Value("${tiingo.token}")
    private String tiingoToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public List<BookmarkResponse.BookmarkSymbolDTO> recommendBookmarkSymbol() {

        try {

            List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

            List<String> symbolList = bookmarkSymbolRepository.findMostFrequentSymbols();

            for (String symbol : symbolList) {

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

                if (dtos != null) {
                    for (ApiResponse.BookmarkSymbolDTO dto : dtos) {
                        //symbol 글자 완전 일치하는것만 가져온다
                        if (dto.getSymbol().equals(search.toUpperCase())) {

                            String logo ;
                            if(dto.getLogo() == null) {
                                logo = LogoUtil.makeLogo(search);
                            }
                            else{
                                logo = dto.getLogo();
                            }

                            LocalDate today = LocalDate.now();
                            LocalDate twoWeeksAgo = today.minusWeeks(2);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

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

                            if (tiingoList.length < 2) {
                                // 이 경우에 대한 에러 처리나 대체 방안이 필요합니다.
                                throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
                            }

                            BookmarkResponse.Price price = BookmarkResponse.Price.builder()
                                    .today(tiingoList[0])
                                    .yesterday(tiingoList[1])
                                    .build();

                            BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                                    dto.getId(),
                                    dto.getSymbol(),
                                    dto.getCompanyName(),
                                    dto.getSector(),
                                    logo,
                                    dto.getMarketType(),
                                    price
                            );

                            bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
                            break;
                        }
                    }
                } else {
                    log.error("symbol: {} 에 대한 API 호출 결과 없음", search);
                }

            }

            return bookmarkSymbolDTOList;

        } catch (Exception e) {
            log.error("recommendBookmark: ", e.getMessage());
            throw new Exception500("recommendBookmark error");
        }
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse.BookmarkSymbolDTO> getLikedBookmarkSymbol(User user) {
        try {

            List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

            List<String> symbolList = bookmarkSymbolRepository.findNonDeletedSymbolByUserId(user.getId());

            for (String symbol : symbolList) {

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

                if (dtos != null) {
                    for (ApiResponse.BookmarkSymbolDTO dto : dtos) {
                        //symbol 글자 완전 일치하는것만 가져온다
                        if (dto.getSymbol().equals(search.toUpperCase())) {

                            String logo ;
                            if(dto.getLogo() == null) {
                                logo = LogoUtil.makeLogo(search);
                            }
                            else{
                                logo = dto.getLogo();
                            }

                            LocalDate today = LocalDate.now();
                            LocalDate twoWeeksAgo = today.minusWeeks(2);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

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

                            if (tiingoList.length < 2) {
                                // 이 경우에 대한 에러 처리나 대체 방안이 필요합니다.
                                throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
                            }

                            BookmarkResponse.Price price = BookmarkResponse.Price.builder()
                                    .today(tiingoList[0])
                                    .yesterday(tiingoList[1])
                                    .build();

                            BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                                    dto.getId(),
                                    dto.getSymbol(),
                                    dto.getCompanyName(),
                                    dto.getSector(),
                                    logo,
                                    dto.getMarketType(),
                                    price
                            );

                            bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
                            break;
                        }
                    }
                } else {
                    log.error("symbol: {} 에 대한 API 호출 결과 없음", search);
                }

            }

            return bookmarkSymbolDTOList;

        } catch (Exception e) {
            log.error("getLikedBookmarkSymbol: ", e.getMessage());
            throw new Exception500("getLikedBookmarkSymbol error");
        }
    }
}
