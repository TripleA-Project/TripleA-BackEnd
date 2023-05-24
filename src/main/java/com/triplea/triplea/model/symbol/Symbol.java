package com.triplea.triplea.model.symbol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Symbol {
    @Id
    private Long id;
    @Column(nullable = false)
    private String symbol;
}
