package com.triplea.triplea.controller;

import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/stocks/index")
    public ResponseEntity<?> getStockIndex() {
        StockResponse.Index index = stockService.getStockIndex();
        return ResponseEntity.ok().body(new ResponseDTO<>(index));
    }
}
