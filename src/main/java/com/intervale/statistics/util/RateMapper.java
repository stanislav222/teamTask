package com.intervale.statistics.util;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.model.entity.RateEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RateMapper {

    public static RateEntity rateDtoIntoRateEntity(RateDto rateDto){

        return RateEntity.builder()
                .sellRate(rateDto.getSellRate())
                .sellIso(rateDto.getSellIso())
                .sellCode(rateDto.getSellCode())
                .buyRate(rateDto.getBuyRate())
                .buyIso(rateDto.getBuyIso())
                .buyCode(rateDto.getBuyCode())
                .quantity(rateDto.getQuantity())
                .name(rateDto.getName())
                .date(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                //.date(LocalDate.now())
                .build();
    }
}
