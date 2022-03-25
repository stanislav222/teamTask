package com.intervale.statistics.schedule.daemon;

import com.intervale.statistics.dao.BookDao;
import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import com.intervale.statistics.model.entity.RateEntity;
import com.intervale.statistics.util.RateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CollectingCourseStatistics {
    private final BookDao bookDao;
    private final AlfaBankExchangeWithWebClient webClient;

   // @Scheduled(cron = "${statistics.notifications.daemon.expiration-report-cron}",
    //        zone = "${statistics.notifications.daemon.timeZone}")
    public void report() {
        List<RateDto> rateDtos = webClient.getTheCurrentCurrencySaleRateAB();          //(List.of(Currency.EUR, Currency.USD, Currency.RUB));
        List<RateEntity> rateEntities = rateDtos.stream()
                .filter(rate -> rate.getName() != null)
                .map(RateMapper::rateDtoIntoRateEntity)
                .collect(Collectors.toList());
        //rateEntities.forEach(bookDao::addRate);
        log.info("Added into DB");
    }
}
