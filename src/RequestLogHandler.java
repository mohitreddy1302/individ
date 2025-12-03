import java.util.Map;

public class RequestLogHandler extends BaseLogHandler {
    private static final String ROUTE_KEY = "request_url";
    private static final String RESPONSE_TIME_KEY = "response_time_ms";
    private static final String STATUS_KEY = "response_status";

    private final RequestAggregator aggregator;

    public RequestLogHandler(RequestAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isRequestLog(logEntry)) {
            processRequestLog(logEntry);
        }
        handleNext(logEntry);
    }

    private void processRequestLog(Map<String, String> logEntry) {
        String route = logEntry.get(ROUTE_KEY);
        String responseTime = logEntry.get(RESPONSE_TIME_KEY);
        String status = logEntry.get(STATUS_KEY);

        if (route == null || responseTime == null || status == null) {
            return;
        }

        try {
            aggregator.addResponseTime(route, Double.parseDouble(responseTime));
            aggregator.addStatusCode(route, Integer.parseInt(status));
        } catch (NumberFormatException ignored) {
            // Ignore malformed numeric values as per requirements
        }
    }

    private boolean isRequestLog(Map<String, String> data) {
        return data.containsKey(ROUTE_KEY)
            && data.containsKey(RESPONSE_TIME_KEY)
            && data.containsKey(STATUS_KEY);
    }
}
