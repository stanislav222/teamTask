package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.CsvGenerationService;
import com.intervale.statistics.sevice.SvgGenerationService;
import com.intervale.statistics.sevice.GeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final GeneratorService generatorService;
    private final SvgGenerationService svgGenerationService;
    private final CsvGenerationService csvGenerationService;

    //Todo: добавить Request param с кол-во дней
    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_PDF_VALUE,
                        "text/csv",
                        "image/svg+xml"
            })
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatistics(@RequestHeader Map<String, String> headers,
                                                                   @PathVariable String title,
                                                                   @PathVariable List<Currency> nameCurrency)
                                                throws BookException, GenerateException {
                                                                   @PathVariable List<Currency> nameCurrency)
            throws BookException{

        SimpleBankCurrencyExchangeRateDto currenciesForPeriodOfTime = bookService
                    .getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime(title, nameCurrency);

        byte[] body = generatorService.createResponseBody(currenciesForPeriodOfTime, headers);

        return new ResponseEntity<>(body, HttpStatus.OK);
        if (headers.containsValue("image/svg+xml")) {
            byte[] svg = svgGenerationService.createSvg(currenciesForPeriodOfTime);
            return new ResponseEntity<>(svg, HttpStatus.OK);
        }
        else if (headers.containsValue("text/csv")) {
            byte[] csv = csvGenerationService.createCsv(currenciesForPeriodOfTime);
            return new ResponseEntity<>(csv, HttpStatus.OK);
        }
        return new ResponseEntity<>(currenciesForPeriodOfTime, HttpStatus.OK);
    }
}

