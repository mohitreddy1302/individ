import java.util.Map;

public class APMLogHandler extends BaseLogHandler {
    private final APMAggregator aggregator;

    public APMLogHandler(APMAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isAPMLog(logEntry)) {
            String metric = logEntry.get("metric");
            String valueStr = logEntry.get("value");
            if (metric != null && valueStr != null) {
                try {
                    double value = Double.parseDouble(valueStr);
                    aggregator.addData(metric, value);
                } catch (NumberFormatException ignored) {}
            }
        }
        handleNext(logEntry);
    }

    private boolean isAPMLog(Map<String, String> data) {
        return data.containsKey("metric") && data.containsKey("value");
    }
}