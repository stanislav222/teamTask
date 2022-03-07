package com.intervale.statistics.sevice;


import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CsvGenerationService {
    public byte[] createCsv(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> dto) throws BookException {
        try {
            File file = new File("./src/main/resources/WEB-INF/csv/" + dto.getTitle() + "(" +
                    DateTimeFormatter.ofPattern("dd-MM-yyyy;HH-mm-ss").format(LocalDateTime.now()) + ").csv");
            file.getParentFile().mkdirs();
            ByteArrayOutputStream byteOutput = writeCsvInByteArray(dto);
            FileOutputStream fileOutput = new FileOutputStream(file);
            byteOutput.writeTo(fileOutput);
            fileOutput.close();
            return byteOutput.toByteArray();
        } catch (IOException e) {
            throw new BookException("Cannot create CSV");
        }
    }

    private ByteArrayOutputStream writeCsvInByteArray(
            SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> dto) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(byteOutput), CSVFormat.Builder.create().setDelimiter(';').build());
        csvPrinter.printRecord("TITLE", "PRICE");
        csvPrinter.printRecord(dto.getTitle(), dto.getPrice());
        csvPrinter.print("DATE");
        Map<String, Map<String, BigDecimal>> dynamicRates = dto.getNationalBankExchangeRate();
        csvPrinter.printRecord(dynamicRates.values().stream().findAny().get().keySet());
        for (String date: dynamicRates.keySet()) {
            csvPrinter.print(date);
            csvPrinter.printRecord(dynamicRates.get(date).values());
        }
        csvPrinter.flush();
        csvPrinter.close();
        return byteOutput;
    }
}
