package be.tbriers;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractLinesChart extends JFrame {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private JFreeChart chart;
    private final Path file;

    @SneakyThrows
    public AbstractLinesChart(Path file, String name) {
        super(name);
        this.file = file;

        add(createChartPanel(), BorderLayout.CENTER);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @SneakyThrows
    public void saveChartAsPNG() {
        ChartUtils.saveChartAsPNG(new File(file.getParent()
                        .toFile()
                        .getAbsolutePath() + File.separator + getFileNameWithoutExtension() + ".png"),
                chart,
                WIDTH,
                HEIGHT);
    }

    @SneakyThrows
    private JPanel createChartPanel() {
        chart = ChartFactory.createXYLineChart(getFileNameWithoutExtension(),
                "Time (m:ss)", "", createDataset());

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();

        for (int i = 0; i < getColors().size(); i++) {
            renderer.setSeriesPaint(i, getColors().get(i));

        }
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesPaint(3, Color.MAGENTA);

        renderer.setLegendItemLabelGenerator(getXySeriesLabelGenerator());
        changeDomainAxis();

        return new ChartPanel(chart);
    }

    private void changeDomainAxis() {
        ((NumberAxis) chart.getXYPlot().getDomainAxis()).setTickUnit(new NumberTickUnit(150));
        ((NumberAxis) chart.getXYPlot().getDomainAxis()).setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                return format(((Double) number).intValue(), toAppendTo);
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return format(((Long) number).intValue(), toAppendTo);
            }

            public StringBuffer format(int number, StringBuffer toAppendTo) {
                return toAppendTo.append(number / 60).append(":").append(String.format("%02d", number % 60));
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    private XYSeriesLabelGenerator getXySeriesLabelGenerator() {
        return new StandardXYSeriesLabelGenerator() {
            @Override
            public String generateLabel(XYDataset dataset, int series) {
                return generateLabel((XYSeriesCollection) dataset, series);
            }

            private String generateLabel(XYSeriesCollection dataset, int series) {
                return String.format("%s (Avg: %.2f)", dataset.getSeriesKey(series), getAverage(dataset, series));
            }

            private double getAverage(XYSeriesCollection dataset, int series) {
                return dataset.getSeries(series).getItems()
                        .stream()
                        .map(xyDataItem -> ((XYDataItem) xyDataItem).getYValue())
                        .mapToDouble(value -> (double) value)
                        .average()
                        .orElse(0.0);
            }
        };
    }

    @SneakyThrows
    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        Map<Comparable<String>, XYSeries> series = getSeries().keySet().stream()
                .map(XYSeries::new)
                .collect(Collectors.toMap(xySeries -> (Comparable<String>) xySeries.getKey(), xySeries -> xySeries));

        try (BufferedReader bufferedReader = Files.newBufferedReader(file)) {
            CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader(getHeaders())
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(bufferedReader)
                    .forEach(record -> getSeries().forEach(
                            (key, value) -> series.get(key)
                                    .add(Double.valueOf(record.get("TIME")),
                                            Double.valueOf(record.get(value)))));
        }

        series.forEach((stringComparable, xySeries) -> dataset.addSeries(xySeries));

        return dataset;
    }

    private String getFileNameWithoutExtension() {
        return StringUtils.substringBefore(file.getFileName().toString(), ".");
    }

    public abstract Map<String, String> getSeries();

    public abstract String[] getHeaders();

    public abstract List<Color> getColors();
}
