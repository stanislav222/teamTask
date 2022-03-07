package com.intervale.statistics.sevice;

import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;

import java.math.BigDecimal;
import java.util.Map;

public interface GeneratorResponseBody {

    byte[] createResponseBody(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> rate)
            throws GenerateException;
}
