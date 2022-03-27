package com.intervale.statistics.util;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.external.alfabank.model.Currency;
import com.intervale.statistics.model.entity.RateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;

class RateMapperTest {

    private RateDto dtoForTest;

    @BeforeEach
    void setupMockWebServer() {
        dtoForTest = new RateDto();
        dtoForTest.setBuyCode(933);
        dtoForTest.setBuyIso("BYN");
        dtoForTest.setBuyRate(new BigDecimal("4.400000"));
        /*for example, Alfa Bank returns the 26th number for 27, because it was a day off*/
        dtoForTest.setDate("26.03.2022");
        dtoForTest.setName("евро");
        dtoForTest.setQuantity(1);
        dtoForTest.setSellCode(978);
        dtoForTest.setSellIso("EUR");
        dtoForTest.setSellRate(new BigDecimal("4.000000"));
    }


    @Test
    void rateDtoIntoRateEntity() {
        RateEntity rateEntity = RateMapper.rateDtoIntoRateEntity(dtoForTest);
        assertThat(rateEntity.getBuyCode(), equalTo(933));
        assertThat(rateEntity.getBuyIso(), equalTo("BYN"));
        assertThat(rateEntity.getBuyRate(), comparesEqualTo(new BigDecimal("4.400000")));
        assertThat(rateEntity.getDate(), equalTo(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        assertThat(rateEntity.getName(), equalTo("евро"));
        assertThat(rateEntity.getQuantity(), equalTo(1));
        assertThat(rateEntity.getSellCode(), equalTo(Currency.EUR.getCurrencyCode()));
        assertThat(rateEntity.getSellIso(), equalTo(Currency.EUR.name()));
        assertThat(rateEntity.getSellRate(), comparesEqualTo(new BigDecimal("4.000000")));
    }
}