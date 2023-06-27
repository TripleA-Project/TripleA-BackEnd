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

    @GetMapping("/symbol/search")//심볼검색
    public ResponseEntity<?> searchSymbol(@RequestParam(value = "symbol") @Valid String symbol){

        List<SymbolResponse.SymbolDTO> symbolDTOList = symbolService.searchSymbol(symbol);

        return ResponseEntity.ok().body(new ResponseDTO<>(symbolDTOList));
    }

    @GetMapping("/symbol")//심볼조회
    public ResponseEntity<?> getSymbol(@RequestParam(value = "symbol") @Valid String symbol){

        List<BookmarkResponse.BookmarkSymbolDTO> bookmarkSymbolDTOS = symbolService.getSymbol(symbol);
        return ResponseEntity.ok().body(new ResponseDTO<>(bookmarkSymbolDTOS));
    }
}
