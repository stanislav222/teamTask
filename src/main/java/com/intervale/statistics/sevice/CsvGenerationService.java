package com.intervale.statistics.sevice;


import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import java.util.Map;

@Service
public class CsvGenerationService {
    public String createCsv(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> dto) throws BookException {
        StringWriter stringWriter = new StringWriter();
        try {
            CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.Builder.create().setDelimiter(';').build());
            csvPrinter.printRecord("TITLE", "PRICE");
            csvPrinter.printRecord(dto.getTitle(), dto.getPrice());
            csvPrinter.print("DATE");
            Map<String, Map<String, BigDecimal>> dynamicRates = dto.getNationalBankExchangeRate();
            csvPrinter.printRecord(dynamicRates.values().stream().findAny().get().keySet());
            for (String date: dynamicRates.keySet()) {
                csvPrinter.print(date);
                csvPrinter.printRecord(dynamicRates.get(date).values());
            }
            return stringWriter.toString();
        } catch (IOException e) {
            throw new BookException("Cannot create CSV");
        }
    }
}
