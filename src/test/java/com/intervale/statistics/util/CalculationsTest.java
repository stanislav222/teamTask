package com.intervale.statistics.util;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.external.alfabank.model.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CalculationsTest {

    private Calculations calculations;
    private NationalRateDto rateDto;
    private LinkedHashMap<String, BigDecimal> booksPriceChanges;

    public static final BigDecimal BLR_TO_RUB_FOR_PRICE_7 = new BigDecimal("194.4444");
    public static final BigDecimal BLR_TO_RUB_FOR_PRICE_8 = new BigDecimal("222.2222");
    public static final BigDecimal BLR_TO_RUB_FOR_PRICE_9 = new BigDecimal("250.0000");

    @BeforeEach
    void setupMockWebServer(){
        calculations = new Calculations();
        /*
        * for the test I use NationalRateDto so the calculation is the same with RateDto
        * */
        rateDto = new NationalRateDto();
        rateDto.setCode(Currency.RUB.getCurrencyCode());
        rateDto.setIso(Currency.RUB.name());
        rateDto.setQuantity(100);
        rateDto.setRate(new BigDecimal("3.6000"));

        booksPriceChanges = new LinkedHashMap<>();
        booksPriceChanges.put("24.03.2022", new BigDecimal(7)); // date : price book
        booksPriceChanges.put("25.03.2022", new BigDecimal(8));
        booksPriceChanges.put("27.03.2022", new BigDecimal(9));
    }

    @Test
    void dateFromNationalRateBefore() {
        //date before date's from HashMap, should take the price for the first date into Map : 7 BLR
        rateDto.setDate("21.03.2022");
        Map<String, BigDecimal> stringBigDecimalMapForNR = calculations
                .getStringBigDecimalMapForNR(booksPriceChanges,
                        List.of(rateDto));
        Assertions.assertNotNull(stringBigDecimalMapForNR);
        Assertions.assertEquals("RUB", stringBigDecimalMapForNR.keySet()
                .iterator()
                .next());
        Assertions.assertEquals(BLR_TO_RUB_FOR_PRICE_7, stringBigDecimalMapForNR
                .values()
                .iterator()
                .next());
    }

    @Test
    void dateFromNationalRateBetween() {
        // date between 25 and 27, should take the price for the 25th : 8 BLR
        rateDto.setDate("26.03.2022");
        Map<String, BigDecimal> stringBigDecimalMapForNR = calculations
                .getStringBigDecimalMapForNR(booksPriceChanges,
                        List.of(rateDto));
        Assertions.assertNotNull(stringBigDecimalMapForNR);
        Assertions.assertEquals("RUB", stringBigDecimalMapForNR.keySet()
                .iterator()
                .next());
        Assertions.assertEquals(BLR_TO_RUB_FOR_PRICE_8, stringBigDecimalMapForNR
                .values()
                .iterator()
                .next());
    }

    @Test
    void dateFromNationalRateSame() {
        // the date is the same as the date from the Map : price 9 BLR
        rateDto.setDate("27.03.2022");
        Map<String, BigDecimal> stringBigDecimalMapForNR = calculations
                .getStringBigDecimalMapForNR(booksPriceChanges,
                        List.of(rateDto));
        Assertions.assertNotNull(stringBigDecimalMapForNR);
        Assertions.assertEquals("RUB", stringBigDecimalMapForNR.keySet()
                .iterator()
                .next());
        Assertions.assertEquals(BLR_TO_RUB_FOR_PRICE_9, stringBigDecimalMapForNR
                .values()
                .iterator()
                .next());
    }

    @Test
    void getLastPriceFromMap() {
        BigDecimal currentPrice = calculations.getCurrentPrice(booksPriceChanges);
        Assertions.assertEquals(currentPrice, new BigDecimal(9));
    }

}