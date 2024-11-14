package be.tbriers;

import lombok.SneakyThrows;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main extends JFrame {

    @SneakyThrows
    public static void main(String[] args) {
        try (Stream<Path> walk = Files.walk(Path.of("/Volumes/NO NAME/ELLIPT/WKO_DATA"))) {
            List<Path> files = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toFile().isHidden())
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .toList();

            if (args.length == 0) {
                files.forEach(file -> {
                    System.out.println(file.toString());
                    EllipticalLinesChart ellipticalLinesChart = new EllipticalLinesChart(file);
                    ellipticalLinesChart.saveChartAsPNG();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    EllipticalLinesChart ellipticalLinesChart = new EllipticalLinesChart(files.getFirst());
                    ellipticalLinesChart.setVisible(true);
                    ellipticalLinesChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                });
            }
        }

        try (Stream<Path> walk = Files.walk(Path.of("/Volumes/NO NAME/CLIMBER/WKO_DATA"))) {
            List<Path> files = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toFile().isHidden())
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .toList();

            if (args.length == 0) {
                files.forEach(file -> {
                    System.out.println(file.toString());
                    ClimberLinesChart climberLinesChart = new ClimberLinesChart(file);
                    climberLinesChart.saveChartAsPNG();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    ClimberLinesChart climberLinesChart = new ClimberLinesChart(files.getFirst());
                    climberLinesChart.setVisible(true);
                    climberLinesChart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                });
            }
        }
    }
}
