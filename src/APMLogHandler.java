import java.util.Map;

public class APMLogHandler extends BaseLogHandler {
    private static final String METRIC_KEY = "metric";
    private static final String VALUE_KEY = "value";

    private final APMAggregator aggregator;

    public APMLogHandler(APMAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isApmLog(logEntry)) {
            String metric = logEntry.get(METRIC_KEY);
            String value = logEntry.get(VALUE_KEY);
            if (metric != null && value != null) {
                try {
                    aggregator.addData(metric, Double.parseDouble(value));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed numeric values as per requirements
                }
            }
        }
        handleNext(logEntry);
    }

    private boolean isApmLog(Map<String, String> data) {
        return data.containsKey(METRIC_KEY) && data.containsKey(VALUE_KEY);
    }
}
