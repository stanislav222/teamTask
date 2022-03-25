package com.intervale.statistics.sevice;

import com.intervale.statistics.dao.BookDao;
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
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDao bookDao;
    private final AlfaBankExchangeWithWebClient alfaBankExchangeClient;
    private final Calculations calc;

    /**
     * getPriceByTitle : цена книги по названию книги из БД
     * @param title Название книги
     * @return Успешное выполнение запроса возвращает - цену в формате BigDecimal
     *         Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */
    public BigDecimal getPriceByTitle(String title) throws BookException {
        Optional<Book> priceByTitle = Optional.ofNullable(bookDao.getCurrentPriceByTitle(title));
        return priceByTitle.map(Book::getCost)
                .orElseThrow(() ->new BookException(String.format("Price not found by title %s", title)));
    }

    /**
     * getPriceByTitle : цены книги по названию книги из БД
     * @param title Название книги
     * @return Успешное выполнение запроса возвращает - цены в формате Map<String, BigDecimal>,
     *     где String - дата, BigDecimal - стоимость на эту дату
     *         Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */
    public Map<String, BigDecimal> getHistoryOfBookChanges(String title) throws BookException {
        return Optional.ofNullable(bookDao.takeTheHistoryOfBookPriceChange(title))
                .orElseThrow(() ->new BookException("Prices empty"));
    }


    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesNB
            (String title, List<Currency> currencies) throws BookException {
        Map<String, BigDecimal> historyOfBookChanges = getHistoryOfBookChanges(title);
        Map<String, Map<String, BigDecimal>> sorted = alfaBankExchangeClient
                .getTheCurrentCurrencySaleRateWithRangeDate(currencies)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> calc.getStringBigDecimalMapForNR(historyOfBookChanges, e.getValue())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return SimpleBankCurrencyExchangeRate.builder()
                .title(title)
                .price(calc.getCurrentPrice(historyOfBookChanges))
                .nationalBankExchangeRate(sorted)
                .build();
    }


    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesAB
            (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        String currenciesName = currencies.stream().map(Enum::name).collect(Collectors.joining((",")));
        Map<String, Map<String, BigDecimal>> sorted = bookDao.getListRate("").stream()
                .filter(rate -> currenciesName.contains(rate.getSellIso()))
                .collect(Collectors.groupingBy(RateEntity::getDate))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> calc.getStringBigDecimalMapForR(priceByTitle, e.getValue())))
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
