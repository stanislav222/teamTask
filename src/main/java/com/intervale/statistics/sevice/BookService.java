package com.intervale.statistics.sevice;

import com.intervale.statistics.dao.BookDao;
import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import com.intervale.statistics.util.Calculations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
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
     *
     * @param title Название книги
     * @return Успешное выполнение запроса возвращает - цены в формате Map<String, BigDecimal>,
     * где String - дата, BigDecimal - стоимость на эту дату
     * Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */

    public Map<String, BigDecimal> getHistoryOfBookChanges(String title) throws BookException {
        Map<String, BigDecimal> stringBigDecimalMap = bookDao.takeTheHistoryOfBookPriceChange(title);
        return Optional.ofNullable(stringBigDecimalMap.size() != 0 ? stringBigDecimalMap : null)
                .orElseThrow(() ->new BookException(String.format("Prices not found by title %s", title)));
    }


    /**
     * getPriceByTitleWithCostInDifferentCurrenciesNB : получить цену по названию с ценой в разных валютах нац банка
     * @param title
     * @param currencies валюты
     * @param date
     * @return успешный запрос возрощает валюты по нац банку
     * @throws BookException Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     */
    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesNB
            (String title, List<Currency> currencies, int date) throws BookException {
        Map<String, BigDecimal> historyOfBookChanges = getHistoryOfBookChanges(title);
        Map<String, Map<String, BigDecimal>> sorted = alfaBankExchangeClient
                .getTheCurrentCurrencySaleRateWithRangeDate(currencies, date)
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


    /**
     * getPriceByTitleWithCostInDifferentCurrenciesAB : получить цену по названию со стоимостью в разных валютах Альфа Б
     * @param title
     * @param currencies валюты
     * @param dayCount счетчик дней
     * @return успешный запрос возрощает курс альфа банка
     * @throws BookException Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     * @throws RateAlfaBankException Ошибка выполнения запроса
     */
    public SimpleBankCurrencyExchangeRate getPriceByTitleWithCostInDifferentCurrenciesAB
            (String title, List<Currency> currencies, Integer dayCount) throws BookException, RateAlfaBankException {
        Map<String, BigDecimal> historyOfBookChanges = getHistoryOfBookChanges(title);
        String currenciesName = currencies.stream().map(Enum::name).collect(Collectors.joining((",")));
        Optional<List<RateEntity>> resultQueryDB = bookDao.getListRate(dayCount);
        List<RateEntity> rateEntityList = resultQueryDB.orElseThrow( () ->
                new RateAlfaBankException("Error getting data from server"));
        Map<String, Map<String, BigDecimal>> sorted = rateEntityList.stream()
                .filter(rate -> currenciesName.contains(rate.getSellIso()))
                .collect(Collectors.groupingBy(RateEntity::getDate))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> calc.getStringBigDecimalMapForR(historyOfBookChanges, e.getValue())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return SimpleBankCurrencyExchangeRate.builder()
                .title(title)
                .price(calc.getCurrentPrice(historyOfBookChanges))
                .nationalBankExchangeRate(sorted)
                .build();
    }
}
