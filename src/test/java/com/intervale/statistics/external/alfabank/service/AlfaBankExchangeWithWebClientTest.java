package com.intervale.statistics.external.alfabank.service;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.dto.NationalRateListResponseDto;
import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.external.WebClientFilter;
import com.intervale.statistics.external.WebClientFilterAdvanced;
import com.intervale.statistics.external.alfabank.model.Currency;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

class AlfaBankExchangeWithWebClientTest {

    private MockWebServer mockWebServer;
    private AlfaBankExchangeWithWebClient exchangeRateClient;

    private static final String DATE = "27.03.2022";

    @BeforeEach
    void setupMockWebServer(){
        mockWebServer = new MockWebServer();
        exchangeRateClient = new AlfaBankExchangeWithWebClient(WebClient.builder()
                .baseUrl(mockWebServer.url("/").url().toString())
                .filter(new WebClientFilter().errorResponseFilter())
                .filter(new WebClientFilterAdvanced())
                .build());
    }

    @Test
    void getTheCurrentCurrencySaleRateAB() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("ratesAB.json"));
        mockWebServer.enqueue(mockResponse);
        List<RateDto> rateAB = exchangeRateClient.
                getTheCurrentCurrencySaleRateAB();
        Assertions.assertNotNull(rateAB);
        Assertions.assertEquals(Currency.EUR.name(), rateAB.get(0).getSellIso());
    }

    @Test
    void getTheCurrentCurrencySaleRateAbWithException() {
        MockResponse mockResponseWithException = new MockResponse()
                .setBody(getJson("ratesAB.json"))
                .setResponseCode(400);
        mockWebServer.enqueue(mockResponseWithException);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            exchangeRateClient.getTheCurrentCurrencySaleRateAB();
        });
        Assertions.assertEquals("API not found", runtimeException.getMessage());
    }

    @Test
    void getTheCurrentCurrencySaleRateWithDate() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("ratesNB.json"));
        mockWebServer.enqueue(mockResponse);
        NationalRateListResponseDto responseDto = exchangeRateClient.
                getTheCurrentCurrencySaleRateWithDate(List.of(Currency.EUR), DATE).block();
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(Currency.EUR.name(), responseDto
                .getRates().iterator().next().getIso());
        Assertions.assertEquals(DATE, responseDto
                .getRates().iterator().next().getDate());
        Assertions.assertEquals(Currency.EUR.getCurrencyCode(), responseDto
                .getRates().iterator().next().getCode());
    }

    @Test
    void getTheCurrentCurrencySaleRateWithException() {
        MockResponse mockResponseWithException = new MockResponse()
                .setBody(getJson("ratesNB.json"))
                .setResponseCode(400);
        mockWebServer.enqueue(mockResponseWithException);
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> {
            exchangeRateClient.getTheCurrentCurrencySaleRateWithDate(List.of(Currency.EUR), DATE).block();
        });
        Assertions.assertEquals("API not found", runtimeException.getMessage());
    }

    @Test
    void getTheCurrentCurrencySaleRateWithRangeDate() {
        MockResponse mockResponse = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getJson("ratesNB.json"));
        mockWebServer.enqueue(mockResponse);
        Map<String, List<NationalRateDto>> withRangeDate = exchangeRateClient
                .getTheCurrentCurrencySaleRateWithRangeDate(List.of(Currency.EUR), 1);
        Assertions.assertNotNull(withRangeDate);
        Assertions.assertEquals(1, withRangeDate.size());
        Assertions.assertEquals(DATE, withRangeDate.keySet().iterator().next());
    }

    private String getJson(String path) {
        try {
            InputStream jsonStream = this.getClass().getClassLoader().getResourceAsStream(path);
            assert jsonStream != null;
            return new String(jsonStream.readAllBytes());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}