package com.intervale.statistics.sevice;

import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;

import java.math.BigDecimal;
import java.util.Map;

public interface ResponseGenerator {

    String getResponseFormat();

    byte[] getBytesArray(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate)
            throws GenerateException ;
}
