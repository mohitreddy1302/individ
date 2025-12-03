import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationAggregator {
    private final Map<String, Integer> severityCounts = new HashMap<>();

    public void addData(String level) {
        severityCounts.merge(level, 1, Integer::sum);
    }

    public Map<String, Object> computeAggregates() {
        return new LinkedHashMap<>(severityCounts);
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }
}
