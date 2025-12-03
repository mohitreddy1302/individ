import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class APMAggregator {
    private final Map<String, List<Double>> metricsData = new HashMap<>();

    public void addData(String metric, double value) {
        metricsData.computeIfAbsent(metric, key -> new ArrayList<>()).add(value);
    }

    public Map<String, Object> computeAggregates() {
        Map<String, Object> result = new TreeMap<>();
        for (Map.Entry<String, List<Double>> entry : metricsData.entrySet()) {
            List<Double> sortedValues = createSortedCopy(entry.getValue());
            result.put(entry.getKey(), buildMetrics(sortedValues));
        }
        return result;
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }

    private List<Double> createSortedCopy(List<Double> values) {
        List<Double> copy = new ArrayList<>(values);
        copy.sort(Double::compareTo);
        return copy;
    }

    private Map<String, Object> buildMetrics(List<Double> sortedValues) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        double min = sortedValues.get(0);
        double max = sortedValues.get(sortedValues.size() - 1);
        double average = sortedValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        metrics.put("minimum", min);
        metrics.put("median", calculateMedian(sortedValues));
        metrics.put("average", average);
        metrics.put("max", max);
        return metrics;
    }

    private double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        int midpoint = size / 2;
        if (size % 2 == 0) {
            double lower = sortedValues.get(midpoint - 1);
            double upper = sortedValues.get(midpoint);
            return (lower + upper) / 2.0;
        }
        return sortedValues.get(midpoint);
    }
}
