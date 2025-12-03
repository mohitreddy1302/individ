import java.util.Map;

public class RequestLogHandler extends BaseLogHandler {
    private final RequestAggregator aggregator;

    public RequestLogHandler(RequestAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handle(Map<String, String> logEntry) {
        if (isRequestLog(logEntry)) {
            String route = logEntry.get("request_url");
            String responseTimeStr = logEntry.get("response_time_ms");
            String statusCodeStr = logEntry.get("response_status");

            if (route != null && responseTimeStr != null && statusCodeStr != null) {
                try {
                    double responseTime = Double.parseDouble(responseTimeStr);
                    int statusCode = Integer.parseInt(statusCodeStr);
                    aggregator.addResponseTime(route, responseTime);
                    aggregator.addStatusCode(route, statusCode);
                } catch (NumberFormatException ignored) {}
            }
        }
        handleNext(logEntry);
    }

    private boolean isRequestLog(Map<String, String> data) {
        return data.containsKey("request_url") && 
               data.containsKey("response_time_ms") && 
               data.containsKey("response_status");
    }
}