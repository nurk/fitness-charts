package be.tbriers;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class MultiLinesChart extends JFrame {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private JFreeChart chart;
    private final Path file;

    @SneakyThrows
    public MultiLinesChart(Path file) {
        super("Crosstrainer");
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
                "Time", "", createDataset());

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesPaint(3, Color.MAGENTA);

        return new ChartPanel(chart);
    }

    @SneakyThrows
    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries level = new XYSeries("Level");
        XYSeries spm = new XYSeries("SPM");
        XYSeries watts = new XYSeries("Watts");
        XYSeries incline = new XYSeries("Incline");

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(Objects.requireNonNull(Main.class.getClassLoader()
                        .getResource("example.csv"))
                .toURI()))) {
            CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader("LEVEL", "SPM", "WATTS", "INCLINE", "HR", "TIME", "CALORIES", "DISTANCE")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(bufferedReader)
                    .forEach(record -> {
                        level.add(Double.valueOf(record.get("TIME")), Double.valueOf(record.get("LEVEL")));
                        spm.add(Double.valueOf(record.get("TIME")), Double.valueOf(record.get("SPM")));
                        watts.add(Double.valueOf(record.get("TIME")), Double.valueOf(record.get("WATTS")));
                        incline.add(Double.valueOf(record.get("TIME")), Double.valueOf(record.get("INCLINE")));
                    });
        }

        dataset.addSeries(level);
        dataset.addSeries(spm);
        dataset.addSeries(watts);
        dataset.addSeries(incline);

        return dataset;
    }

    private String getFileNameWithoutExtension() {
        return StringUtils.substringBefore(file.getFileName().toString(), ".");
    }
}
