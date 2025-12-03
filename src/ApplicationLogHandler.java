import java.util.Map;

public class ApplicationLogHandler extends BaseLogHandler {
    private static final String LEVEL_KEY = "level";
    private static final String MESSAGE_KEY = "message";

    private final ApplicationAggregator aggregator;

    public ApplicationLogHandler(ApplicationAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isApplicationLog(logEntry)) {
            String level = logEntry.get(LEVEL_KEY);
            if (level != null) {
                aggregator.addData(level);
            }
        }
        handleNext(logEntry);
    }

    private boolean isApplicationLog(Map<String, String> data) {
        return data.containsKey(LEVEL_KEY) && data.containsKey(MESSAGE_KEY);
    }
}
