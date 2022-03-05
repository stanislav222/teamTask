package com.intervale.statistics.sevice;

import com.intervale.statistics.dao.BookDaoWithJdbcTemplate;
import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.statistics.model.Book;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;
    private final AlfaBankExchangeWithWebClient alfaBankExchangeClient;

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

    //может тут проще можно наговнокодил кажись
    public SimpleBankCurrencyExchangeRateDto getPriceByTitleWithCostInDifferentCurrenciesForPeriodOfTime
            (String title, List<Currency> currencies) throws BookException {
        BigDecimal priceByTitle = getPriceByTitle(title);
        Map<String, List<NationalRateDto>> rateWithRangeDate = alfaBankExchangeClient
                .getTheCurrentCurrencySaleRateWithRangeDate(currencies);
        Map<String, Map<String, BigDecimal>> collect = rateWithRangeDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getStringBigDecimalMap(priceByTitle, e.getValue())));
        Map<String, Map<String, BigDecimal>> sortedResult = collect.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return SimpleBankCurrencyExchangeRateDto.builder()
                .title(title)
                .price(priceByTitle)
                .nationalBankExchangeRate(sortedResult)
                .build();
    }


    private Map<String, BigDecimal> getStringBigDecimalMap(BigDecimal priceByTitle, List<NationalRateDto> saleRate) {
        return saleRate.stream()
                    .collect(Collectors.toMap(NationalRateDto::getIso, i -> priceByTitle
                            .divide(i.getRate()
                                    .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }
}
