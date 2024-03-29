package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.CheckMembership;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.core.util.provide.symbol.MoyaSymbolProvider;
import com.triplea.triplea.core.util.provide.symbol.TiingoSymbolProvider;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import com.triplea.triplea.model.bookmark.BookmarkSymbol;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkSymbolService {

    private final UserRepository userRepository;
    private final BookmarkSymbolRepository bookmarkSymbolRepository;
    private final CustomerRepository customerRepository;
    private final StepPaySubscriber subscriber;
    private final MoyaSymbolProvider moyaSymbolProvider;
    private final TiingoSymbolProvider tiingoSymbolProvider;

    @Value("${moya.token}")
    private String moyaToken;

    @Value("${tiingo.token}")
    private String tiingoToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public List<BookmarkResponse.BookmarkSymbolDTO> recommendBookmarkSymbol() {

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

                    if (dto == null)
                        continue;

                    //symbol 글자 완전 일치하는것만 가져온다
                    if (dto.getSymbol().equals(search.toUpperCase())) {

                        BookmarkResponse.Price price = getPrice(search);

                        String logo = dto.getLogo();
                        if (logo == null) {
                            logo = LogoUtil.makeLogo(search);
                        }
                        String symbolcopy = dto.getSymbol();
                        if (symbolcopy == null)
                            symbolcopy = symbol;

                        String companyName = dto.getCompanyName();
                        if (companyName == null) {
                            String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
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
            } else {
                //Moya API에서 검색이 안 되는 경우 Tiingo API에서 조회
                String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
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

                ApiResponse.TiingoSymbol tiingoSymbols = tiingoSymbolResponse.getBody();
                String logo = LogoUtil.makeLogo(search);
                BookmarkResponse.Price price = getPrice(search);

                BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                        null,
                        tiingoSymbols.getTicker(),
                        tiingoSymbols.getName(),
                        null,
                        logo,
                        tiingoSymbols.getExchangeCode(),
                        price
                );

                bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
//                log.error("symbol: {} 에 대한 API 호출 결과 없음", search);
            }
        }

        return bookmarkSymbolDTOList;

    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse.BookmarkSymbolDTO> getLikedBookmarkSymbol(User user) {

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOList = new ArrayList<>();

        List<BookmarkSymbol> symbolList = bookmarkSymbolRepository.findNonDeletedSymbolByUserId(user.getId());

        for (BookmarkSymbol symbol : symbolList) {

            String search = symbol.getSymbol();

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
            boolean flag = false;
            if (dtos != null) {

                for (ApiResponse.BookmarkSymbolDTO dto : dtos) {
                    if (null == dto)
                        continue;

                    //symbol 글자 완전 일치하는것만 가져온다
                    if (dto.getSymbol().equals(search.toUpperCase())) {
                        BookmarkResponse.Price price = getPrice(search);

                        String logo = dto.getLogo();
                        if (logo == null) {
                            logo = LogoUtil.makeLogo(search);
                        }
                        String symbolcopy = dto.getSymbol();
                        if (symbolcopy == null)
                            symbolcopy = search;

                        String companyName = dto.getCompanyName();
                        if (companyName == null) {
                            String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
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
                                symbol.getId(),
                                dto.getSymbol(),
                                companyName,
                                dto.getSector(),
                                logo,
                                dto.getMarketType(),
                                price
                        );

                        bookmarkSymbolDTOList.add(bookmarkSymbolDTO);

                        flag = false;
                        break;
                    }else flag = true;
                }
            }
            if(flag) {

                //Moya API에서 검색이 안 되는 경우 Tiingo API에서 조회
                String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>";
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

                ApiResponse.TiingoSymbol tiingoSymbols = tiingoSymbolResponse.getBody();
                String logo = LogoUtil.makeLogo(search);
                BookmarkResponse.Price price = getPrice(search);

                BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                        symbol.getId(),
                        tiingoSymbols.getTicker(),
                        tiingoSymbols.getName(),
                        null,
                        logo,
                        tiingoSymbols.getExchangeCode(),
                        price
                );

                bookmarkSymbolDTOList.add(bookmarkSymbolDTO);
//                log.error("symbol: {} 에 대한 API 호출 결과 없음", search);
            }

        }

        return bookmarkSymbolDTOList;
    }

    // 관심 심볼 생성
    @Transactional
    public void saveLikeSymbol(Long userId, String symbol) {
        symbol = symbol.toUpperCase();
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception400("Bad-Request", "잘못된 userID입니다."));
        SymbolRequest.MoyaSymbol symbolInfo = moyaSymbolProvider.getSymbolInfo(symbol);

        if (symbolInfo.getSymbol() == null) {
            symbolInfo = tiingoSymbolProvider.getSymbolInfo(symbol);
        }


        User.Membership membership = CheckMembership.getMembership(userPS, customerRepository, subscriber);
        if (membership == User.Membership.BASIC) {
            Integer count = bookmarkSymbolRepository.countAllByUser(userPS);
            if (count >= 3) throw new Exception400("benefit", "혜택을 모두 소진했습니다");
        }
        String finalSymbol = symbolInfo.getSymbol();
        bookmarkSymbolRepository.findBySymbolAndUser(finalSymbol, userPS).ifPresentOrElse(bookmarkSymbol -> {
            if (!bookmarkSymbol.isDeleted()) throw new Exception400("symbol", "이미 관심 설정한 심볼입니다");
            bookmarkSymbol.bookmark();
        }, () -> bookmarkSymbolRepository.save(BookmarkSymbol.builder()
                .user(userPS)
                .symbol(finalSymbol)
                .isDeleted(false)
                .build()));

    }

    // 관심 심볼 삭제
    @Transactional
    public void deleteLikeSymbol(User user, Long id) {
        BookmarkSymbol bookmarkSymbol = bookmarkSymbolRepository.findNonDeletedByIdAndUser(id, user)
                .orElseThrow(() -> new Exception400("symbol", "해당 Symbol이 존재하지 않습니다."));
        bookmarkSymbol.deleteBookmark();
    }

    private BookmarkResponse.Price getPrice(String search) {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksAgo = today.minusWeeks(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

        String todayStr = today.format(formatter);
        String twoWeeksAgoStr = twoWeeksAgo.format(formatter);

        String tiingoUrl = "https://api.tiingo.com/tiingo/daily/<ticker>/prices";
        tiingoUrl = tiingoUrl.replace("<ticker>", search);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tiingoUrl)
                .queryParam("token", tiingoToken)
                .queryParam("startDate", twoWeeksAgoStr)
                .queryParam("endDate", todayStr)
                .queryParam("sort", "-date");

        String url = builder.toUriString();

        ResponseEntity<ApiResponse.Tiingo[]> tiingoresponse;

        try {
            tiingoresponse = restTemplate.getForEntity(url, ApiResponse.Tiingo[].class);
        } catch (RestClientException e) {
            log.error(url, e.getMessage());
            throw new Exception500("API 호출 실패");
        }

        ApiResponse.Tiingo[] tiingoList = tiingoresponse.getBody();

//        if (null == tiingoList) continue;

        if (tiingoList.length < 2) {
            throw new Exception500("Tiingo API 응답이 2개 이상의 데이터를 포함하지 않습니다.");
        }

        return BookmarkResponse.Price.builder()
                .today(tiingoList[0])
                .yesterday(tiingoList[1])
                .build();
    }
}
