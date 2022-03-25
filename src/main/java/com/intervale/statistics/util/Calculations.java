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

    public Map<String, BigDecimal> getStringBigDecimalMapForR(BigDecimal priceByTitle, List<RateEntity> saleRate) {

        return saleRate.stream()
                .collect(Collectors.toMap(RateEntity::getSellIso, price -> priceByTitle
                        .divide(price.getBuyRate()
                                .divide(BigDecimal.valueOf(price.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }

    private BigDecimal withHistory(Map<String, BigDecimal> booksCurrency, NationalRateDto i) {
        BigDecimal bigDecimal = null;
        Set<String> keys = booksCurrency.keySet();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (booksCurrency.containsKey(i.getDate())) {
            bigDecimal = booksCurrency.get(i.getDate());
        }else {
            String firstDate = keys.iterator().next();
            for (String key: keys){
                LocalDate localDate = LocalDate.parse(key, formatter);
                int compareTo = localDate.compareTo(LocalDate.parse(i.getDate(), formatter));
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

    public BigDecimal getCurrentPrice(Map<String, BigDecimal> historyOfBookChanges) {
        return historyOfBookChanges.entrySet().stream().reduce((first, second) -> second).orElse(null).getValue();
    }
}
