package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.FormatFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("api/v1/book")
@Validated
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
            @RequestParam (value = "dayCount", defaultValue = "${alfa-bank.setting.number-of-recent-days}")
            @Min(value = 1, message = "The number of days must be greater than 0") Integer dayCount,
            @PathVariable String title,
            @PathVariable List<Currency> nameCurrency)
            throws BookException, GenerateException, RateAlfaBankException {

        SimpleBankCurrencyExchangeRate currencies = bookService
                    .getPriceByTitleWithCostInDifferentCurrenciesNB(title, nameCurrency, dayCount);


        //SimpleBankCurrencyExchangeRate currencies = bookService
         //       .getPriceByTitleWithCostInDifferentCurrenciesAB(title, nameCurrency,dayCount);

        byte[] bytesArray = formatFactory.getFormat(header).getBytesArray(currencies);
        return new ResponseEntity<>(bytesArray, HttpStatus.OK);
    }

}

