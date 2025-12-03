import java.util.*;

public class APMAggregator {
    private final Map<String, List<Double>> metricsData = new HashMap<>();

    public void addData(String metric, double value) {
        metricsData.computeIfAbsent(metric, k -> new ArrayList<>()).add(value);
    }

    public Map<String, Object> computeAggregates() {
        Map<String, Object> result = new TreeMap<>();
        
        for (Map.Entry<String, List<Double>> entry : metricsData.entrySet()) {
            List<Double> values = entry.getValue();
            values.sort(Double::compareTo);
            
            double min = values.get(0);
            double max = values.get(values.size() - 1);
            double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            int n = values.size();
            double median;
            if (n % 2 == 0) {
                double lower = values.get(n/2 - 1);
                double upper = values.get(n/2);
                median = (lower + upper) / 2.0;
            } else {
                median = values.get(n/2);
            }

            
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("minimum", min);
            metrics.put("median",  median);
            metrics.put("average", avg);
            metrics.put("max",     max);
            
            result.put(entry.getKey(), metrics);
        }
        
        return result;
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }
}