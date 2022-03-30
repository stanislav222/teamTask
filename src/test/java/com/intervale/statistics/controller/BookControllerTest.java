package com.intervale.statistics.controller;

import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.BookService;
import com.intervale.statistics.sevice.FormatFactory;
import com.intervale.statistics.sevice.impl.JsonGenerationServiceImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private BookController bookController;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private BookService bookService;
    @MockBean
    private FormatFactory formatFactory;
    @MockBean
    private JsonGenerationServiceImpl generator;

    @Test
    void getBytesFromController() throws Exception {

        byte[] testBytesArray = new byte[5];
        SimpleBankCurrencyExchangeRate testCurrencies = SimpleBankCurrencyExchangeRate.builder()
                .title("1")
                .price(new BigDecimal("3.3"))
                .nationalBankExchangeRate(Map.of("12.12.2022", Map.of("USD", new BigDecimal("1"))))
                .build();

        when(bookService.getPriceByTitleWithCostInDifferentCurrenciesAB(any(), any(), any()))
                .thenReturn(testCurrencies);
        when(formatFactory.getFormat(anyString())).thenReturn(generator);
        when(generator.getBytesArray(any())).thenReturn(testBytesArray);

        // Устанавливаем header Accept
        List<MediaType> acceptMediaTypesList = Arrays.asList(MediaType.APPLICATION_JSON);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptMediaTypesList);
        HttpEntity<String> httpRequestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = testRestTemplate.exchange(
                "/api/v1/book/price/stat/{title}/{nameCurrency}",
                HttpMethod.GET,
                httpRequestEntity,
                byte[].class,
                "Harry Potter", "RUB", "USD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(testBytesArray, response.getBody());
        verify(bookService, times(1))
                .getPriceByTitleWithCostInDifferentCurrenciesAB(any(), any(), any());
        verify(formatFactory, times(1)).getFormat(anyString());
        verify(generator, times(1)).getBytesArray(any());
    }
}