package com.intervale.statistics.sevice;

import com.intervale.statistics.dao.BookDaoWithJdbcTemplate;
import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import com.intervale.statistics.util.Calculations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;
    private final AlfaBankExchangeWithWebClient alfaBankExchangeClient;
    private final Calculations calculations;

    /**
     * getPriceByTitle : цена книги по названию книги из БД
     * @param title Название книги
     * @return Успешное выполнение запроса возвращает - цену в формате BigDecimal
     *         Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */
    public BigDecimal getPriceByTitle(String title) throws BookException {
        Optional<Book> priceByTitle = Optional.ofNullable(bookDaoWithJdbcTemplate.getPriceByTitle(title));
        return priceByTitle.map(Book::getCost)
                .orElseThrow(() ->new BookException(String.format("Price not found by title %s", title)));
    }


    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesNB
            (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        Map<String, Map<String, BigDecimal>> sorted = alfaBankExchangeClient
                .getTheCurrentCurrencySaleRateWithRangeDate(currencies)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> calculations.getStringBigDecimalMapForNR(priceByTitle, e.getValue())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return SimpleBankCurrencyExchangeRate.builder()
                .title(title)
                .price(priceByTitle)
                .nationalBankExchangeRate(sorted)
                .build();
    }

    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesAB
            (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        String currenciesName = currencies.stream().map(Enum::name).collect(Collectors.joining((",")));
        Map<String, Map<String, BigDecimal>> sorted = bookDaoWithJdbcTemplate.getListRate("").stream()
                .filter(rate -> currenciesName.contains(rate.getSellIso()))
                .collect(Collectors.groupingBy(RateEntity::getDate))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> calculations.getStringBigDecimalMapForR(priceByTitle, e.getValue())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return SimpleBankCurrencyExchangeRate.builder()
                .title(title)
                .price(priceByTitle)
                .nationalBankExchangeRate(sorted)
                .build();
    }
}
