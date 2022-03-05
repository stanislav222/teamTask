package com.intervale.statistics.external.alfabank.service;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.dto.NationalRateListResponseDto;
import com.intervale.statistics.external.alfabank.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Qualifier("webClientAlfaBank")
    private final WebClient webClient;

    public List<NationalRateDto> getTheCurrentCurrencySaleRate(List<Currency> currencyList) {
        List<Integer> collect = currencyList.stream().map(Currency::getCurrencyCode).collect(Collectors.toList());
        String codeCurrencies = collect.stream().map(String::valueOf).collect(Collectors.joining((",")));
        String url = "/partner/1.0.1/public/nationalRates?currencyCode="+ codeCurrencies;
        NationalRateListResponseDto responseDto = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        return responseDto.getRates();
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
        String url = "/partner/1.0.1/public/nationalRates?currencyCode="+ codeCurrencies;
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
        LocalDate endDate = startDate.minusDays(10);
        return endDate.datesUntil(startDate)
                .map(dtf::format)
                .collect(Collectors.toList());
    }
}
