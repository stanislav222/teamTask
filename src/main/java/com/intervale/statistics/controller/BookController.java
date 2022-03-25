package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.FormatFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final FormatFactory formatFactory;

    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_PDF_VALUE,
                        "text/csv",
                        "image/svg+xml"
            })
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatistics(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String header,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
                                                throws BookException, GenerateException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                    .getPriceByTitleWithCostInDifferentCurrenciesNB(title, nameCurrency);

       // SimpleBankCurrencyExchangeRate currencies = bookService
        //        .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }


   /* @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatisticsJson(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String header,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
            throws BookException, GenerateException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }

    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatisticsPdf(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String header,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
            throws BookException, GenerateException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }

    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = "text/csv")
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatisticsCsv(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String header,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
            throws BookException, GenerateException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }

    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = "image/svg+xml")
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatisticsSvg(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String header,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
            throws BookException, GenerateException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }*/

}

