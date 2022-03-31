package com.intervale.statistics.sevice;

import com.intervale.statistics.dao.BookDao;
import com.intervale.statistics.dao.BookDaoWithJdbcTemplate;
import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import com.intervale.statistics.util.Calculations;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private final BookDao bookDao = Mockito.mock(BookDaoWithJdbcTemplate.class);
    private final AlfaBankExchangeWithWebClient alfaBankExchangeWithWebClient = Mockito
            .mock(AlfaBankExchangeWithWebClient.class);
    private final Calculations calculations = new Calculations();
    private final BookService bookService = new BookService(bookDao, alfaBankExchangeWithWebClient, calculations);

    private Map<String, BigDecimal> testHistoryOfBookChanges = new LinkedHashMap<>();
    private Book book;
    private Map<String, List<NationalRateDto>> testMapNationalRate = new HashMap<>();
    private List<Currency> currencyList;
    private List<NationalRateDto> nationalRateDtoList;
    private List<RateEntity> rateEntityList;
    private SimpleBankCurrencyExchangeRate testSimpleBankCurrencyExchangeRate;

    @BeforeEach
    void init() {

        book = new Book();
        book.setCost(new BigDecimal("15"));

        testHistoryOfBookChanges.put("10.03.2022", new BigDecimal("50"));
        testHistoryOfBookChanges.put("15.03.2022", new BigDecimal("100"));
        testHistoryOfBookChanges.put("20.03.2022", new BigDecimal("300"));

        currencyList = Arrays.asList(Currency.RUB, Currency.USD);

        NationalRateDto nationalRateDto1 = new NationalRateDto();
        nationalRateDto1.setRate(new BigDecimal("3.2"));
        nationalRateDto1.setIso("RUB");
        nationalRateDto1.setCode(643);
        nationalRateDto1.setQuantity(100);
        nationalRateDto1.setDate("15.03.2022");
        nationalRateDto1.setName("российский рубль");

        NationalRateDto nationalRateDto2 = new NationalRateDto();
        nationalRateDto2.setRate(new BigDecimal("3.5"));
        nationalRateDto2.setIso("USD");
        nationalRateDto2.setCode(840);
        nationalRateDto2.setQuantity(1);
        nationalRateDto2.setDate("15.03.2022");
        nationalRateDto2.setName("доллар США");
        nationalRateDtoList = Arrays.asList(nationalRateDto1, nationalRateDto2);

        testMapNationalRate.put("15.03.2022", nationalRateDtoList);

        RateEntity rateEntity1 = RateEntity.builder()
                .sellIso("RUB")
                .buyRate(new BigDecimal("3.2"))
                .quantity(100)
                .date("15.03.2022")
                .build();
        RateEntity rateEntity2 = RateEntity.builder()
                .sellIso("USD")
                .buyRate(new BigDecimal("3.5"))
                .quantity(1)
                .date("15.03.2022")
                .build();
        rateEntityList = Arrays.asList(rateEntity1, rateEntity2);

        Map<String, BigDecimal> testCurrency = new LinkedHashMap<>();
        testCurrency.put("USD", new BigDecimal("28.5714"));
        testCurrency.put("RUB", new BigDecimal("3125.0000"));

        testSimpleBankCurrencyExchangeRate = SimpleBankCurrencyExchangeRate.builder()
                .title("Harry")
                .price(new BigDecimal("300"))
                .nationalBankExchangeRate(Map.of("15.03.2022", testCurrency))
                .build();
    }

    @Test
    void getPriceByTitle() throws BookException {
        when(bookDao.getCurrentPriceByTitle(anyString())).thenReturn(book);

        assertNotNull(bookService.getPriceByTitle(anyString()));
        assertEquals(new BigDecimal("15"), bookService.getPriceByTitle(anyString()));
        verify(bookDao, times(2)).getCurrentPriceByTitle(anyString());
    }

    @Test
    void getPriceByTitleThrowBookException() {

        when(bookDao.getCurrentPriceByTitle(anyString())).thenReturn(null);

        String title = "Harry";
        String message = "Price not found by title " + title;
        BookException bookException = assertThrows(BookException.class,
                () -> bookService.getPriceByTitle(title));
        assertEquals(message, bookException.getMessage());
        verify(bookDao, times(1)).getCurrentPriceByTitle(anyString());
    }
    /*
    @Ignore
    @Test
    void getHistoryOfBookChanges() throws BookException {

        when(bookDao.takeTheHistoryOfBookPriceChange(anyString())).thenReturn(testHistoryOfBookChanges);

        assertNotNull(bookService.getHistoryOfBookChanges(anyString()));
        assertEquals(testHistoryOfBookChanges, bookService.getHistoryOfBookChanges(anyString()));
        verify(bookDao, times(2)).takeTheHistoryOfBookPriceChange(anyString());
    }

    @Ignore
    @Test
    void getPriceByTitleWithCostInDifferentCurrenciesNB() throws BookException {

        when(bookDao.takeTheHistoryOfBookPriceChange(anyString())).thenReturn(testHistoryOfBookChanges);
        when(alfaBankExchangeWithWebClient.getTheCurrentCurrencySaleRateWithRangeDate(any(), any()))
                .thenReturn(testMapNationalRate);
        String title = "Harry";

        assertEquals(testHistoryOfBookChanges, bookService.getHistoryOfBookChanges(title));
        assertEquals(testSimpleBankCurrencyExchangeRate,
                bookService.getPriceByTitleWithCostInDifferentCurrenciesNB(title, currencyList, 5));

    }*/

    @Test
    void getHistoryOfBookChangesThrowBookException() {
        when(bookDao.takeTheHistoryOfBookPriceChange(anyString())).thenReturn(null);
        NullPointerException bookException = assertThrows(NullPointerException.class,
                () -> bookService.getHistoryOfBookChanges(anyString()));
        verify(bookDao, times(1)).takeTheHistoryOfBookPriceChange(anyString());
    }

    @Test
    void getPriceByTitleWithCostInDifferentCurrenciesAB() throws RateAlfaBankException, BookException {
        when(bookDao.takeTheHistoryOfBookPriceChange(anyString())).thenReturn(testHistoryOfBookChanges);
        when(bookDao.getListRate(anyInt())).thenReturn(Optional.ofNullable(rateEntityList));
        String title = "Harry";

        assertEquals(testHistoryOfBookChanges, bookService.getHistoryOfBookChanges(anyString()));
        assertEquals(testSimpleBankCurrencyExchangeRate,
                bookService.getPriceByTitleWithCostInDifferentCurrenciesAB(title, currencyList, 5));
    }
}