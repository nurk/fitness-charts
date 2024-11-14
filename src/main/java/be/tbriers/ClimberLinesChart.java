package be.tbriers;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ClimberLinesChart extends AbstractLinesChart {

    public ClimberLinesChart(Path file) {
        super(file, "Stairmaster");
    }

    @Override
    public Map<String, String> getSeries() {
        return Map.of("Level", "LEVEL", "SPM", "SPM", "Incline", "INCLINE");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{"LEVEL", "SPM", "MET", "INCLINE", "HR", "TIME", "CALORIES", "FLOORS"};
    }

    @Override
    public List<Color> getColors() {
        return List.of(Color.RED, Color.GREEN, Color.BLUE);
    }
}
