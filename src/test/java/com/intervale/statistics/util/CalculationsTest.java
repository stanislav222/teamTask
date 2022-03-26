package com.intervale.statistics.util;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class CalculationsTest {

    public static void main(String[] args) {

        SimpleObject simpleObject3 = new SimpleObject();
        simpleObject3.setDate("21.03.2022");
        simpleObject3.setIso("RUB");
        simpleObject3.setQuantity(100);
        simpleObject3.setRate(new BigDecimal(1.0000));
        List<SimpleObject> simpleObjects = List.of(simpleObject3);
       // System.out.println(simpleObjects);

        Map<String, BigDecimal> booksPriceChanges = new LinkedHashMap<>();
        //booksPriceChanges.put("20.03.2022", new BigDecimal(1.0000));
        //booksPriceChanges.put("22.03.2022", new BigDecimal(2.0000));
        booksPriceChanges.put("24.03.2022", new BigDecimal(1));
        booksPriceChanges.put("25.03.2022", new BigDecimal(2));
        booksPriceChanges.put("26.03.2022", new BigDecimal(3));


       // System.out.println(booksPriceChanges);

        Map<String, BigDecimal> stringBigDecimalMapForNR = getStringBigDecimalMapForNR(booksPriceChanges, simpleObjects);
        System.out.println(stringBigDecimalMapForNR);
    }

    public static Map<String, BigDecimal> getStringBigDecimalMapForNR(Map<String, BigDecimal> booksCurrency, List<SimpleObject> saleRate) {
        return saleRate.stream()
                .collect(Collectors.toMap(SimpleObject::getIso, i ->
                        getObject(booksCurrency, i)
                        .divide(i.getRate()
                                .divide(BigDecimal.valueOf(i.getQuantity()), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)));
    }

    private static BigDecimal getObject(Map<String, BigDecimal> booksCurrency, SimpleObject i) {
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

    @Data
    private static class SimpleObject {
        String date;

        private String iso;

        private BigDecimal rate;

        private Integer quantity;

    }
}