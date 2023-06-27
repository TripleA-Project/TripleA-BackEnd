package com.triplea.triplea.model.symbol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity @Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "symbol_tb")
public class Symbol {
    @Id
    private Long id;
    @Column(nullable = false)
    private String symbol;
}
