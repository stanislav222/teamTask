package com.intervale.statistics.sevice.impl;

import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.ResponseGenerator;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.svg.converter.SvgConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Map;

@Component(value = "pdfGenerator")
public class PdfGenerationServiceImpl implements ResponseGenerator {

    private final String PDF_DIRECTORY_PATH = "./src/main/resources/WEB-INF/pdf";
    private final String PDF_FILE_PATH = "./src/main/resources/WEB-INF/pdf/report.pdf";
    private final String BACKGROUND_IMAGE_PATH = "./src/main/resources/background.jpg";
    private final String SVG_FILE_PATH = "./src/main/resources/WEB-INF/img/result.svg";
    private static final String FORMAT_TYPE = "application/pdf";

    @Qualifier("svgGenerationServiceImpl")
    @Autowired(required = false)
    private ResponseGenerator responseGenerator;

    @Override
    public String getResponseFormat() {
        return FORMAT_TYPE;
    }

    @Override
    public byte[] getBytesArray(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate)
            throws GenerateException {

        File pdfDirectory = new File(PDF_DIRECTORY_PATH);
        if (!pdfDirectory.exists()){
            pdfDirectory.mkdir();
        }

        //создаём pdf файл
        createPdfFile(rate);

        // Преобразуем pdf файл в массив byte
        byte[] data = new byte[0];
        File filePdf = new File(PDF_FILE_PATH);
        if (filePdf.exists()) {
            try(InputStream inputStream = new FileInputStream(filePdf)) {
                data = new byte[(int) filePdf.length()];
                data = inputStream.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void createPdfFile(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate)
            throws GenerateException {

        PdfWriter pdfWriter = null;
        try {
            pdfWriter = new PdfWriter(PDF_FILE_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (pdfWriter == null){
            throw new GenerateException("Ошибка при создании pdf файла.");
        }

        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        PageSize pageSize = PageSize.A4;
        Document document = new Document(pdfDocument, pageSize);

        // Устанавливаем фон
        PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
        try {
            canvas.addImageFittedIntoRectangle(ImageDataFactory.create(BACKGROUND_IMAGE_PATH), pageSize, false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Заполняем pdf информацией
        String pdfTitle = "Statistic of price book in currency";
        Paragraph paragraphPDFTitle = new Paragraph(pdfTitle).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24F).setBold();
        document.add(paragraphPDFTitle);

        List list = new List().setFontSize(14F);
        list.add("Title of book: " + rate.getTitle());
        list.add("Price of book BYN: " + rate.getPrice());
        document.add(list);

        // Создаём таблицу
        Map<String, Map<String, BigDecimal>> rateInfo = rate.getNationalBankExchangeRate();

        //Узнаём количество валют сожержащихся в rate
        int countCurrency = rateInfo.values().stream()
                .map(e -> e.entrySet().size())
                .findFirst()
                .get();

        int columnCount = countCurrency + 2;
        Table table = new Table(columnCount).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(11F);

        // Шапка таблицы
        table.addCell("N");
        table.addCell("Date");
        rateInfo.values().stream().findFirst().get().forEach((currency, price) -> table.addCell(currency));

        // Содержание таблицы
        int numberRow = 1;
        for (Map.Entry<String, Map<String, BigDecimal>> entry : rateInfo.entrySet()) {
            table.addCell(String.valueOf(numberRow));
            table.addCell(entry.getKey());
            for (Map.Entry<String, BigDecimal> entryCurrency : entry.getValue().entrySet()) {
                table.addCell(String.valueOf(entryCurrency.getValue()));
            }
            numberRow += 1;
        }
        document.add(table);

        if(responseGenerator != null) {
            responseGenerator.getBytesArray(rate);
        }
        //Если файл Result.svg существует, добавляем svg файл в конец pdf
        File svgFile = new File(SVG_FILE_PATH);
        if (svgFile.exists()) {
            try (FileInputStream svg = new FileInputStream(SVG_FILE_PATH)) {
                // Устанавливаем фон
                PdfCanvas canvasNewPage = new PdfCanvas(pdfDocument.addNewPage());
                canvasNewPage.addImageFittedIntoRectangle(ImageDataFactory.create(BACKGROUND_IMAGE_PATH),
                        pageSize, false);
                Image converted = SvgConverter.convertToImage(svg, pdfDocument);
                document.add(converted);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document.close();
    }
}
