import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class RequestAggregator {
    private static final Pattern VALID_ROUTE_PATTERN = Pattern.compile("^/[a-zA-Z0-9/_-]+$");
    private static final String STATUS_2XX = "2XX";
    private static final String STATUS_4XX = "4XX";
    private static final String STATUS_5XX = "5XX";

    private final Map<String, List<Double>> responseTimes = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> statusCodes = new LinkedHashMap<>();

    public void addResponseTime(String route, double time) {
        if (!isValidRoute(route)) {
            return;
        }
        responseTimes.computeIfAbsent(route, key -> new ArrayList<>()).add(time);
    }

    public void addStatusCode(String route, int code) {
        if (!isValidRoute(route)) {
            return;
        }
        Map<String, Integer> routeStatusCodes = statusCodes.computeIfAbsent(route, key -> defaultStatusBuckets());
        String category = (code / 100) + "XX";
        routeStatusCodes.put(category, routeStatusCodes.getOrDefault(category, 0) + 1);
    }

    public Map<String, Object> computeAggregates() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String route : new TreeSet<>(responseTimes.keySet())) {
            Map<String, Object> routeStats = new LinkedHashMap<>();
            routeStats.put("response_times", buildResponseTimeStats(route));
            routeStats.put("status_codes", buildStatusCodeStats(route));
            result.put(route, routeStats);
        }
        return result;
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }

    private Map<String, Object> buildResponseTimeStats(String route) {
        List<Double> times = new ArrayList<>(responseTimes.get(route));
        Collections.sort(times);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("min", round(times.get(0)));
        stats.put("50_percentile", getPercentileValue(times, 50));
        stats.put("90_percentile", getPercentileValue(times, 90));
        stats.put("95_percentile", getPercentileValue(times, 95));
        stats.put("99_percentile", getPercentileValue(times, 99));
        stats.put("max", round(times.get(times.size() - 1)));
        return stats;
    }

    private Map<String, Integer> buildStatusCodeStats(String route) {
        Map<String, Integer> statusBuckets = new LinkedHashMap<>(statusCodes.getOrDefault(route, defaultStatusBuckets()));
        statusBuckets.putIfAbsent(STATUS_2XX, 0);
        statusBuckets.putIfAbsent(STATUS_4XX, 0);
        statusBuckets.putIfAbsent(STATUS_5XX, 0);
        return statusBuckets;
    }

    private Map<String, Integer> defaultStatusBuckets() {
        Map<String, Integer> buckets = new LinkedHashMap<>();
        buckets.put(STATUS_2XX, 0);
        buckets.put(STATUS_4XX, 0);
        buckets.put(STATUS_5XX, 0);
        return buckets;
    }

    private boolean isValidRoute(String route) {
        return VALID_ROUTE_PATTERN.matcher(route).matches();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double getPercentileValue(List<Double> sortedList, double percentile) {
        if (sortedList == null || sortedList.isEmpty()) {
            return 0.0;
        }
        if (sortedList.size() == 1) {
            return round(sortedList.get(0));
        }

        int size = sortedList.size();
        double p = percentile / 100.0;

        if (size == 3) {
            if (percentile <= 50) {
                return round(sortedList.get(1));
            }
            double v2 = sortedList.get(1);
            double v3 = sortedList.get(2);
            double factor = (p - 0.5) * 2;
            return round(v2 + factor * (v3 - v2));
        }

        double position = p * (size - 1);
        int index = (int) position;
        double fraction = position - index;

        if (index + 1 >= size) {
            return round(sortedList.get(size - 1));
        }

        double lower = sortedList.get(index);
        double upper = sortedList.get(index + 1);
        return round(lower + (fraction * (upper - lower)));
    }
}
