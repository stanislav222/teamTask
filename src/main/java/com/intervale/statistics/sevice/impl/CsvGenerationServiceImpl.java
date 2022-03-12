package com.intervale.statistics.sevice.impl;


import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.ResponseGenerator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CsvGenerationServiceImpl implements ResponseGenerator {

    private static final String FORMAT_TYPE = "text/csv";

    @Override
    public String getResponseFormat() {
        return FORMAT_TYPE;
    }

    @Override
    public byte[] getBytesArray(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate) throws GenerateException {
        try {
            File file = new File("./src/main/resources/WEB-INF/csv/" + rate.getTitle() + "(" +
                    DateTimeFormatter.ofPattern("dd-MM-yyyy;HH-mm-ss").format(LocalDateTime.now()) + ").csv");
            file.getParentFile().mkdirs();
            ByteArrayOutputStream byteOutput = writeCsvInByteArray(rate);
            FileOutputStream fileOutput = new FileOutputStream(file);
            byteOutput.writeTo(fileOutput);
            fileOutput.close();
            return byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GenerateException("Can`t create csv.");
        }
    }

    private ByteArrayOutputStream writeCsvInByteArray(
            SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> dto) throws IOException {
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
