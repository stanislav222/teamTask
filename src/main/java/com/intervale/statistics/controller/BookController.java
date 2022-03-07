package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.CsvGenerationService;
import com.intervale.statistics.sevice.SvgGenerationService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
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
                                                                   @PathVariable List<Currency> nameCurrency) throws BookException {

        SimpleBankCurrencyExchangeRateDto currenciesForPeriodOfTime = bookService
                    .getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime(title, nameCurrency);

        if (headers.containsValue("image/svg+xml")) {
            byte[] svg = svgGenerationService.createSvg(currenciesForPeriodOfTime);
            return new ResponseEntity<>(svg, HttpStatus.OK);
        }
        else if (headers.containsValue("text/csv")) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + currenciesForPeriodOfTime.getTitle() + ".csv");
            return new ResponseEntity<>(csvGenerationService.createCsv(currenciesForPeriodOfTime), responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(currenciesForPeriodOfTime, HttpStatus.OK);
    }


}

