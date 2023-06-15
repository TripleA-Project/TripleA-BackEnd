package com.triplea.triplea.controller;

import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.service.SymbolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class SymbolController {

    private final SymbolService symbolService;

    @GetMapping("/symbol")
    public ResponseEntity<?> getSymbol(@RequestParam(value = "symbol") @Valid String symbol){

        List<SymbolResponse.SymbolDTO> symbolDTOList = symbolService.getSymbol(symbol);

        return ResponseEntity.ok().body(new ResponseDTO<>(symbolDTOList));
    }
}
