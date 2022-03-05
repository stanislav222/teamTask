package com.intervale.statistics.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleBankCurrencyExchangeRateDto<T> {
    @NonNull
    private String title;
    @NonNull
    private BigDecimal price;
    @NonNull
    private T nationalBankExchangeRate;
}
