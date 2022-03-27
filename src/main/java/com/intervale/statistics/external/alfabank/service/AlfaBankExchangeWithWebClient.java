package com.intervale.statistics.external.alfabank.service;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.dto.NationalRateListResponseDto;
import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.dto.RateListResponseDto;
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

    private static final String NATIONAL_BANK_ROUT = "/partner/1.0.1/public/nationalRates?currencyCode=";
    private static final String NATIONAL_BANK_ROUT_DATE = "&date={date}";
    private static final String ALFA_BANK_ROUT = "/partner/1.0.1/public/rates";

    @Qualifier("webClientAlfaBank")
    private final WebClient webClient;

    public List<RateDto> getTheCurrentCurrencySaleRateAB(){
        RateListResponseDto rateList = webClient
                .get()
                .uri(ALFA_BANK_ROUT)
                .retrieve()
                .bodyToMono(RateListResponseDto.class)
                .blockOptional()
                .orElseThrow(() ->
                        new RuntimeException("The json format has changed or API Error: AB"));
        return rateList.getRates();
    }

    public Map<String, List<NationalRateDto>> getTheCurrentCurrencySaleRateWithRangeDate
            (List<Currency> currencyList, int dateCount) {
        List<String> dateRange = getDatesBetween(dateCount);
        Flux<NationalRateListResponseDto> nationalRateListResponseDtoFlux =
                Flux.fromIterable(dateRange)
                .flatMap(date ->
                        getTheCurrentCurrencySaleRateWithDate(currencyList, date));
        Set<NationalRateListResponseDto> block = nationalRateListResponseDtoFlux
                .collect(Collectors.toSet()).block();
        if(block == null){
             throw new RuntimeException("The json format has changed or API Error: NB");
        }
        return block.stream()
                .flatMap(rate -> rate.getRates().stream())
                .collect(Collectors.groupingBy(NationalRateDto::getDate));
    }

    public Mono<NationalRateListResponseDto> getTheCurrentCurrencySaleRateWithDate
            (List<Currency> currencyList, String date) {
        String codeCurrencies = currencyList.stream()
                .map(Currency::getCurrencyCode)
                .map(String::valueOf)
                .collect(Collectors.joining((",")));
        String url = NATIONAL_BANK_ROUT + codeCurrencies;
        return webClient
                .get()
                .uri(url + NATIONAL_BANK_ROUT_DATE, date)
                .retrieve()
                .bodyToMono(NationalRateListResponseDto.class);

    }

    private List<String> getDatesBetween(int dateCount) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(dateCount);
        return endDate.datesUntil(startDate)
                .map(dtf::format)
                .collect(Collectors.toList());
    }
}
