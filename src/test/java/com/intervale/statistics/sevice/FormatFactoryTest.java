package com.intervale.statistics.sevice;

import com.intervale.statistics.sevice.impl.CsvGenerationServiceImpl;
import com.intervale.statistics.sevice.impl.JsonGenerationServiceImpl;
import com.intervale.statistics.sevice.impl.PdfGenerationServiceImpl;
import com.intervale.statistics.sevice.impl.SvgGenerationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FormatFactoryTest {
    
    private final FormatFactory formatFactory;
    private final JsonGenerationServiceImpl jsonGenerationService;
    private final CsvGenerationServiceImpl csvGenerationService;
    private final PdfGenerationServiceImpl pdfGenerationService;
    private final SvgGenerationServiceImpl svgGenerationService;

    @Autowired
    FormatFactoryTest(FormatFactory formatFactory, JsonGenerationServiceImpl jsonGenerationService, 
                      CsvGenerationServiceImpl csvGenerationService, PdfGenerationServiceImpl pdfGenerationService, 
                      SvgGenerationServiceImpl svgGenerationService) {
        this.formatFactory = formatFactory;
        this.jsonGenerationService = jsonGenerationService;
        this.csvGenerationService = csvGenerationService;
        this.pdfGenerationService = pdfGenerationService;
        this.svgGenerationService = svgGenerationService;
    }

    @Test
    void getFormat() {
        ResponseGenerator resultGenerator = formatFactory.getFormat("application/json");
        assertSame(jsonGenerationService, resultGenerator);

        resultGenerator = formatFactory.getFormat("text/csv");
        assertSame(csvGenerationService, resultGenerator);

        resultGenerator = formatFactory.getFormat("application/pdf");
        assertSame(pdfGenerationService, resultGenerator);

        resultGenerator = formatFactory.getFormat("image/svg+xml");
        assertSame(svgGenerationService, resultGenerator);
    }
}