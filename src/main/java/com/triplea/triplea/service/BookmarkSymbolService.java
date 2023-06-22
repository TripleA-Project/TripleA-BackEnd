package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.model.bookmark.BookmarkSymbol;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkSymbolService {

    private final BookmarkSymbolRepository bookmarkSymbolRepository;

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
                                throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
                            }

                            BookmarkResponse.Price price = BookmarkResponse.Price.builder()
                                    .today(tiingoList[0])
                                    .yesterday(tiingoList[1])
                                    .build();

                            String logo = dto.getLogo();
                            if(logo == null) {
                                logo = LogoUtil.makeLogo(search);
                            }
                            String symbolcopy = dto.getSymbol();
                            if(symbolcopy == null)
                                symbolcopy = symbol;

                            String companyName = dto.getCompanyName();
                            if(companyName == null){
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
                                throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
                            }

                            BookmarkResponse.Price price = BookmarkResponse.Price.builder()
                                    .today(tiingoList[0])
                                    .yesterday(tiingoList[1])
                                    .build();

                            String logo = dto.getLogo();
                            if(logo == null) {
                                logo = LogoUtil.makeLogo(search);
                            }
                            String symbolcopy = dto.getSymbol();
                            if(symbolcopy == null)
                                symbolcopy = symbol;

                            String companyName = dto.getCompanyName();
                            if(companyName == null){
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

                                companyName = tiingoSymbolResponse.getBody().getName();
                            }

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

    @Transactional
    public void insertSymbol(Long id, User user) {

        Optional<BookmarkSymbol> nonDeletedBySymbolIdAndUserId = bookmarkSymbolRepository.findNonDeletedBySymbolIdAndUserId(id, user.getId());
        if(nonDeletedBySymbolIdAndUserId.isPresent()){
            log.error("Attempted to add a symbol that already exists. symbolID: " + id + ", user: " + user.getEmail());
            throw new Exception400("BookmarkSymbol", "symbol ID " + id + " already exists");
        }

        BookmarkSymbol bookmarkSymbol = BookmarkSymbol.builder()
            .id(id)
            .user(user)
            .isDeleted(false)
            .build();

        try {
            bookmarkSymbolRepository.save(bookmarkSymbol);
        } catch (DataAccessException e) {
            log.error("Database error when inserting symbol", e);
            throw new Exception500("Database error");
        }
    }

    @Transactional
    public void deleteSymbol(Long id, User user) {

        try{
            Optional<BookmarkSymbol> bookmarkSymbolPS = bookmarkSymbolRepository.findNonDeletedBySymbolIdAndUserId(id, user.getId());
            if(false == bookmarkSymbolPS.isPresent()){
                log.error("symbol not found for symbol {} and user {}", id, user);
                throw new Exception400("symbol", "Symbol not found");
            }

            bookmarkSymbolPS.get().deleteBookmark();

        }catch(DataAccessException e){
            log.error("Database error when deleting bookmark", e);
            throw new Exception500("Database error");
        }
    }
}
