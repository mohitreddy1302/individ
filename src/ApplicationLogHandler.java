import java.util.Map;

public class ApplicationLogHandler extends BaseLogHandler {
    private final ApplicationAggregator aggregator;

    public ApplicationLogHandler(ApplicationAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isApplicationLog(logEntry)) {
            String level = logEntry.get("level");
            if (level != null) {
                aggregator.addData(level);
            }
        }
        handleNext(logEntry);
    }

    private boolean isApplicationLog(Map<String, String> data) {
        return data.containsKey("level") && data.containsKey("message");
    }
}