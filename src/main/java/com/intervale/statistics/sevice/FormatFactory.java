package com.intervale.statistics.sevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FormatFactory {

    private final Map<String, ResponseGenerator> viewerMap;

    /**
     * FormatFactory : Фабрика форматов
     * @param viewers Генератор ответов
     */
    @Autowired
    private FormatFactory(List<ResponseGenerator> viewers) {
        viewerMap =  viewers.stream().collect(Collectors
                .toUnmodifiableMap(ResponseGenerator::getResponseFormat,
                        Function.identity()));
    }

    /**
     * getFormat : получить формат
     * @param formatType
     * @return
     */
    public ResponseGenerator getFormat(String formatType) {
        return Optional.ofNullable(viewerMap.get(formatType))
                .orElseThrow(IllegalArgumentException::new);
    }
}
