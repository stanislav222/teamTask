package com.intervale.statistics.sevice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Service
public class GeneratorService {

    @Qualifier(value = "pdfGenerator")
    private final GeneratorResponseBody pdfGenerator;
    private final SvgGenerationService svgGenerationService;
    private final CsvGenerationService csvGenerationService;
    // Todo  Немного переделать   private final GeneratorResponseBody svgGenerator;
    // Todo     private final GeneratorResponseBody csvGenerator;
    private final ObjectMapper objectMapper;

    public byte[] createResponseBody(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> rate,
                                     Map<String, String> headers) throws GenerateException {

        byte[] body = new byte[0];

        if (headers.containsValue("image/svg+xml")) {
            // ToDo немного переделать под svgGenerator.createResponseBody(rate);
            return svgGenerationService.createSvg(rate);
        }
        if (headers.containsValue("application/pdf")) {

            //Заускаем, чтоб в pdf добавить svg файл со свежими данными
            svgGenerationService.createSvg(rate);
            return pdfGenerator.createResponseBody(rate);
        }
        if (headers.containsValue("text/csv")) {
            return csvGenerationService.createCsv(rate);   // ToDo Заглушка, ждём реализацию csvGenerator.createResponseBody(rate);
        }
        if (headers.containsValue("application/json")) {
            String stringJson = "";
            try {
                stringJson = objectMapper.writeValueAsString(rate);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return stringJson.getBytes();
        }

        return body;
    }
}
