package com.intervale.statistics.sevice.impl;

import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.model.domain.SimpleBankCurrencyExchangeRate;
import com.intervale.statistics.sevice.ResponseGenerator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class SvgGenerationServiceImpl implements ResponseGenerator {

    private static final String FORMAT_TYPE = "image/svg+xml";
    private static final String SVG_DIRECTORY_PATH = "./src/main/resources/WEB-INF/img/";
    private static final String SVG_FILE_NAME = "result.svg";

    /**
     * getResponseFormat : получить формат ответа image/svg+xml
     * @return image/svg+xml
     */
    @Override
    public String getResponseFormat() {
        return FORMAT_TYPE;
    }

    /**
     * getBytesArray : получить массив байтов
     * @param rate SimpleBankCurrencyExchangeRate : Простой банковский курс обмена валюты
     * @return возрощает массив байтов
     * @throws GenerateException GenerateException : Генерировать исключение
     */
    @Override
    public byte[] getBytesArray(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate) throws GenerateException {
        JFreeChart chart = createChart(rate);
        SVGGraphics2D g2 = new SVGGraphics2D(900, 1000);
        Rectangle r = new Rectangle(0, 0, 900, 1000);
        chart.draw(g2, r);
        File fDir = new File(SVG_DIRECTORY_PATH);
        if (!fDir.exists()){
            fDir.mkdirs();
        }
        File svgFile = new File(SVG_DIRECTORY_PATH + SVG_FILE_NAME);

        byte[] data = new byte[0];
        try {
            SVGUtils.writeToSVG(svgFile, g2.getSVGElement());
            InputStream inputStream = new FileInputStream(svgFile);
            data = new byte[(int) svgFile.length()];
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * createChart : создать диаграмму
     * @param rate SimpleBankCurrencyExchangeRate : Простой банковский курс обмена валюты
     * @return возрощает диаграмму
     */
    private JFreeChart createChart(SimpleBankCurrencyExchangeRate<Map<String, Map<String, BigDecimal>>> rate) {
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        rate.getNationalBankExchangeRate().forEach((date, stringBigDecimalMap) -> {
            stringBigDecimalMap.forEach((currency, bigDecimal) -> {
                dataset.add(bigDecimal, null, currency, date);
            });
        });
        JFreeChart chart = ChartFactory.createLineChart(
                "Changing the price of the " + rate.getTitle() + " book Cost in BLR: " +rate.getPrice(),
                "Date",
                "different currency", dataset);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        StatisticalBarRenderer renderer = new StatisticalBarRenderer();
        plot.setRenderer(renderer);
        ChartUtils.applyCurrentTheme(chart);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        return chart;
    }
}
