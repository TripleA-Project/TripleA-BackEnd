package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.model.symbol.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SymbolService {
    @Value("${moya.token}")
    private String moyaToken;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public List<SymbolResponse.SymbolDTO> getSymbol(String symbol){

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

        return symbolDTOList;
    }
}