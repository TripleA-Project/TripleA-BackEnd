package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.model.bookmark.BookmarkSymbolRepository;
import com.triplea.triplea.model.symbol.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.transaction.annotation.Transactional;
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

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public List<BookmarkResponse.BookmarkSymbolDTO> recommendBookmark() {

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
                    log.error("https://api.moya.ai/stock", e.getMessage());
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

                            BookmarkResponse.BookmarkSymbolDTO bookmarkSymbolDTO = new BookmarkResponse.BookmarkSymbolDTO(
                                    dto.getId(),
                                    dto.getSymbol(),
                                    dto.getCompanyName(),
                                    dto.getSector(),
                                    logo,
                                    dto.getMarketType()
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
}
