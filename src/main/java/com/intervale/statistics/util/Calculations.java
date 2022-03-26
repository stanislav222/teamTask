package com.intervale.statistics.util;

import com.intervale.statistics.dto.NationalRateDto;
import com.intervale.statistics.model.entity.RateEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Calculations {

    public Map<String, BigDecimal> getStringBigDecimalMapForNR(Map<String, BigDecimal> booksCurrency, List<NationalRateDto> saleRate) {
        return saleRate.stream()
                .collect(Collectors.toMap(NationalRateDto::getIso, i ->
                        withHistory(booksCurrency, i)
                        .divide(i.getRate()
                                .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }

    public Map<String, BigDecimal> getStringBigDecimalMapForR(Map<String, BigDecimal> booksCurrency, List<RateEntity> saleRate) {

        return saleRate.stream()
                .collect(Collectors.toMap(RateEntity::getSellIso, i ->
                        withHistory(booksCurrency, i)
                        .divide(i.getBuyRate()
                                .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }

    private BigDecimal withHistory(Map<String, BigDecimal> booksCurrency, Object obj) {
        BigDecimal bigDecimal = null;
        Set<String> keys = booksCurrency.keySet();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = getDate(obj);
        if (booksCurrency.containsKey(date)) {
            bigDecimal = booksCurrency.get(date);
        }else {
            String firstDate = keys.iterator().next();
            for (String key: keys){
                LocalDate localDate = LocalDate.parse(key, formatter);
                int compareTo = localDate.compareTo(LocalDate.parse(date, formatter));
                if (compareTo < 0) {
                    bigDecimal = booksCurrency.get(key);
                }else {
                    bigDecimal = booksCurrency.get(firstDate);
                    return bigDecimal;
                }
            }
        }
        return bigDecimal;
    }

    private String getDate(Object obj) {
        String date = null;
        if (obj instanceof NationalRateDto) {
             date = ((NationalRateDto) obj).getDate();
        }
        if (obj instanceof RateEntity) {
            date = ((RateEntity) obj).getDate();
        }
        return date;
    }

    public BigDecimal getCurrentPrice(Map<String, BigDecimal> historyOfBookChanges) {
        return Objects.requireNonNull(historyOfBookChanges
                .entrySet()
                .stream()
                .reduce((first, second) -> second)
                .orElse(null))
                .getValue();
    }
}
