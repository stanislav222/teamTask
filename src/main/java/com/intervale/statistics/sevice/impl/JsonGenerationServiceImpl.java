package com.intervale.statistics.sevice.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.ResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JsonGenerationServiceImpl implements ResponseGenerator {

    private final ObjectMapper objectMapper;

    private static final String FORMAT_TYPE = "application/json";

    @Override
    public String getResponseFormat() {
        return FORMAT_TYPE;
    }

    @Override
    public byte[] getBytesArray(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate) throws GenerateException {
        String stringJson = "";
        try {
            stringJson = objectMapper.writeValueAsString(rate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new GenerateException("Can`t create json");
        }
        return stringJson.getBytes();
    }
}
