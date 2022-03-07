package com.intervale.statistics.sevice;

import com.intervale.statistics.model.dto.SimpleBankCurrencyExchangeRateDto;
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
import java.util.UUID;

@Service
public class SvgGenerationService {

    private JFreeChart createChart(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> rate) {
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        rate.getNationalBankExchangeRate().forEach((date, stringBigDecimalMap) -> {
            stringBigDecimalMap.forEach((currency, bigDecimal) -> {
                dataset.add(bigDecimal, null, currency, date);
            });
        });
        JFreeChart chart = ChartFactory.createLineChart(
                "Changing the price of the " + rate.getTitle() + " book Cost in BLR:" +rate.getPrice(),
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


    public byte[] createSvg(SimpleBankCurrencyExchangeRateDto<Map<String, Map<String, BigDecimal>>> simpleBankCurrencyExchangeRateDto) {
        JFreeChart chart = createChart(simpleBankCurrencyExchangeRateDto);
        SVGGraphics2D g2 = new SVGGraphics2D(900, 1000);
        Rectangle r = new Rectangle(0, 0, 900, 1000);
        chart.draw(g2, r);
        File fDir = new File("./src/main/resources/WEB-INF/img/");
        if (!fDir.exists()){
            fDir.mkdir();
        }
        File f = new File("./src/main/resources/WEB-INF/img/result.svg");

        byte[] data = new byte[0];
        try {
            SVGUtils.writeToSVG(f, g2.getSVGElement());
            InputStream inputStream = new FileInputStream(f);
            data = new byte[(int) f.length()];
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
