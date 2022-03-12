package com.intervale.statistics.external.alfabank.service;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.dto.NationalRateListResponseDto;
import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.dto.RateListResponseDto;
import com.intervale.statistics.external.alfabank.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlfaBankExchangeWithWebClient {

    private static final String NATIONAL_BANK_ROUT = "/partner/1.0.1/public/nationalRates?currencyCode=";
    private static final String ALFA_BANK_ROUT = "";

    @Qualifier("webClientAlfaBank")
    private final WebClient webClient;

    @Value("${alfa-bank.setting.number-of-recent-days}")
    private int numberOfDays;

    public List<RateDto> getTheCurrentCurrencySaleRateAB(List<Currency> currencyList) {
        String url = ALFA_BANK_ROUT;
        RateListResponseDto rateList = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(RateListResponseDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        return rateList.getRates();
    }

    public Map<String, List<NationalRateDto>> getTheCurrentCurrencySaleRateWithRangeDate(List<Currency> currencyList) {
        List<String> dateRange = getDatesBetween();
        Flux<NationalRateListResponseDto> nationalRateListResponseDtoFlux = Flux.fromIterable(dateRange)
                .flatMap(date -> getTheCurrentCurrencySaleRateWithDate(currencyList, date));
        Set<NationalRateListResponseDto> block = nationalRateListResponseDtoFlux.collect(Collectors.toSet()).block();
        assert block != null;
        return block.stream()
                .flatMap(rate -> rate.getRates().stream())
                .collect(Collectors.groupingBy(NationalRateDto::getDate));
    }

    public Mono<NationalRateListResponseDto> getTheCurrentCurrencySaleRateWithDate(List<Currency> currencyList, String date) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = NATIONAL_BANK_ROUT + codeCurrencies;
        return webClient
                .get()
                .uri(url + "&date={date}", date)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class);

    }

    private List<String> getDatesBetween() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.now();
        System.out.println(startDate);
        LocalDate endDate = startDate.minusDays(numberOfDays);
        return endDate.datesUntil(startDate)
                .map(dtf::format)
                .collect(Collectors.toList());
    }
}
