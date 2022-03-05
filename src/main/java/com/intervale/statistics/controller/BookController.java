package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.SvgGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final SvgGenerationService svgGenerationService;


    //Todo: добавить Request param с кол-во дней
    @GetMapping(value = "/price/stat/{title}/{nameCurrency}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPriceByTitleWithCurrencyStatistics(@PathVariable String title,
                                                                   @PathVariable List<Currency> nameCurrency) throws BookException {
        SimpleBankCurrencyExchangeRateDto currenciesForPeriodOfTime = bookService
                .getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime(title, nameCurrency);
        svgGenerationService.createSvg(currenciesForPeriodOfTime);
        return new ResponseEntity<>(currenciesForPeriodOfTime, HttpStatus.OK);
    }

}

