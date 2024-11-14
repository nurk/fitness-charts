package be.tbriers;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class EllipticalLinesChart extends AbstractLinesChart {

    public EllipticalLinesChart(Path file) {
        super(file, "Crosstrainer");
    }

    @Override
    public Map<String, String> getSeries() {
        return Map.of("Level", "LEVEL", "SPM", "SPM", "Watts", "WATTS", "Incline", "INCLINE");
    }

    @Override
    public String[] getHeaders() {
        return new String[]{"LEVEL", "SPM", "WATTS", "INCLINE", "HR", "TIME", "CALORIES", "DISTANCE"};
    }

    @Override
    public java.util.List<Color> getColors() {
        return List.of(Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA);
    }
}
