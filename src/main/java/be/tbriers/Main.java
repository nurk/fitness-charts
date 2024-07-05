package be.tbriers;

import lombok.SneakyThrows;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main extends JFrame {

    @SneakyThrows
    public static void main(String[] args) {
        List<Path> files = Files.walk(Path.of("/workspace/private/fitness-charts"))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".csv"))
                .toList();


        if (args.length == 0) {
            MultiLinesChart multiLinesChart = new MultiLinesChart(files.get(0));
            multiLinesChart.saveChartAsPNG();
        } else {

            SwingUtilities.invokeLater(() -> {
                MultiLinesChart multiLinesChart = new MultiLinesChart(files.get(0));
                multiLinesChart.setVisible(true);
                multiLinesChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            });
        }
    }
}
