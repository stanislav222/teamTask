package com.intervale.statistics.schedule.daemon;

import com.intervale.statistics.dao.BookDao;
import com.intervale.statistics.external.alfabank.service.AlfaBankExchangeWithWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
public class CollectingCourseStatistics {
    private final BookDao bookDao;
    private final AlfaBankExchangeWithWebClient webClient;

    @Scheduled(cron = "${statistics.notifications.daemon.expiration-report-cron}",
            zone = "${statistics.notifications.daemon.timeZone}")
    public void report() {
        log.info("Added into DB");
        /*List<RateDto> rateDtos = webClient.getTheCurrentCurrencySaleRateAB(List.of(Currency.EUR, Currency.USD, Currency.RUB));
        rateDtos.forEach(i -> {
            bookDao.addRate(i);
            log.info("Added into DB {}", i);
        });
         */
    }
}
