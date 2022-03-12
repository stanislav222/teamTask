package com.intervale.statistics.model.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleBankCurrencyExchangeRate<T> {
    @NonNull
    private String title;
    @NonNull
    private BigDecimal price;
    @NonNull
    private T nationalBankExchangeRate;
}
