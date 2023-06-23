package com.triplea.triplea.controller;

import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    // 주가 지수 조회
    @GetMapping("/stocks/index")
    public ResponseEntity<?> getStockIndex() {
        StockResponse.Index index = stockService.getStockIndex();
        return ResponseEntity.ok().body(new ResponseDTO<>(index));
    }

    @GetMapping("/auth/stocks")
    public ResponseEntity<?> getSymbol(@RequestParam(value = "symbol") String symbol,
                                       @RequestParam(value = "startDate") String startDate,
                                       @RequestParam(value = "endDate") String endDate,
                                       @RequestParam(value = "resampleFreq") String resampleFreq,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails
    ){

        StockResponse.StockInfoDTO stockInfoDTO = stockService.getChart(symbol, startDate, endDate, resampleFreq, myUserDetails.getUser());

        return ResponseEntity.ok().body(new ResponseDTO<>(stockInfoDTO));
    }

}
