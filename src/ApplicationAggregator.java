import java.util.*;

public class ApplicationAggregator {
    private final Map<String, Integer> severityCounts = new HashMap<>();

    public void addData(String level) {
        severityCounts.merge(level, 1, Integer::sum);
    }

    public Map<String, Object> computeAggregates() {
        return new HashMap<>(severityCounts);
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }
}