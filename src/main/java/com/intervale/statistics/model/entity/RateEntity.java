package com.intervale.statistics.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateEntity {

    @Column(value = "sell_rate")
    private BigDecimal sellRate;

    @Column(value = "sell_iso")
    private String sellIso;

    @Column(value = "sell_code")
    private Integer sellCode;

    @Column(value = "buy_rate")
    private BigDecimal buyRate;

    @Column(value = "buy_iso")
    private String buyIso;

    @Column(value = "buy_code")
    private Integer buyCode;

    @Column(value = "quantity")
    private Integer quantity;

    @Column(value = "name")
    private String name;

    @Column(value = "date")
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String date;
}
