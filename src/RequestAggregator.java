import java.util.*;
import java.util.regex.Pattern;

public class RequestAggregator {
    private final Map<String, List<Double>> responseTimes = new HashMap<>();
    private final Map<String, Map<String, Integer>> statusCodes = new HashMap<>();

    // Regular expression to validate route names
    private static final Pattern VALID_ROUTE_PATTERN = Pattern.compile("^/[a-zA-Z0-9/_-]+$");

    public void addResponseTime(String route, double time) {
        if (!isValidRoute(route)) {
            return; // Skip invalid routes
        }
        responseTimes.computeIfAbsent(route, k -> new ArrayList<>()).add(time);
    }

    public void addStatusCode(String route, int code) {
        if (!isValidRoute(route)) {
            return; // Skip invalid routes
        }
        Map<String, Integer> routeStatusCodes = statusCodes.computeIfAbsent(route, k -> {
            Map<String, Integer> codes = new LinkedHashMap<>();
            codes.put("2XX", 0);
            codes.put("4XX", 0);
            codes.put("5XX", 0);
            return codes;
        });

        String category = (code / 100) + "XX";
        routeStatusCodes.put(category, routeStatusCodes.getOrDefault(category, 0) + 1);
    }

    private boolean isValidRoute(String route) {
        return VALID_ROUTE_PATTERN.matcher(route).matches();
    }

    // Method to round values to 2 decimal places
    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double getPercentileValue(List<Double> sortedList, double percentile) {
        if (sortedList == null || sortedList.isEmpty()) {
            return 0.0;
        }
        if (sortedList.size() == 1) {
            return roundToTwoDecimalPlaces(sortedList.get(0));
        }

        int size = sortedList.size();
        double p = percentile / 100.0;

        if (size == 3) {
            if (percentile <= 50) {
                return roundToTwoDecimalPlaces(sortedList.get(1)); // median for p50
            } else {
                double v2 = sortedList.get(1);
                double v3 = sortedList.get(2);
                double factor = (p - 0.5) * 2; // Interpolation factor for higher percentiles
                return roundToTwoDecimalPlaces(v2 + factor * (v3 - v2));
            }
        }

        // Standard percentile calculation
        double position = p * (size - 1);
        int index = (int) position;
        double fraction = position - index;

        if (index + 1 >= size) {
            return roundToTwoDecimalPlaces(sortedList.get(size - 1));
        }

        double lower = sortedList.get(index);
        double upper = sortedList.get(index + 1);
        return roundToTwoDecimalPlaces(lower + (fraction * (upper - lower)));
    }

    public Map<String, Object> computeAggregates() {
        Map<String, Object> result = new LinkedHashMap<>();

        for (String route : new TreeSet<>(responseTimes.keySet())) {
            List<Double> times = new ArrayList<>(responseTimes.get(route));
            Collections.sort(times);

            Map<String, Object> routeStats = new LinkedHashMap<>();
            Map<String, Object> responseTimeStats = new LinkedHashMap<>();

            responseTimeStats.put("min", roundToTwoDecimalPlaces(times.get(0)));
            responseTimeStats.put("50_percentile", getPercentileValue(times, 50));
            responseTimeStats.put("90_percentile", getPercentileValue(times, 90));
            responseTimeStats.put("95_percentile", getPercentileValue(times, 95));
            responseTimeStats.put("99_percentile", getPercentileValue(times, 99));
            responseTimeStats.put("max", roundToTwoDecimalPlaces(times.get(times.size() - 1)));

            routeStats.put("response_times", responseTimeStats);

            Map<String, Integer> routeStatusCodes = statusCodes.getOrDefault(route, new LinkedHashMap<>());
            routeStatusCodes.putIfAbsent("2XX", 0);
            routeStatusCodes.putIfAbsent("4XX", 0);
            routeStatusCodes.putIfAbsent("5XX", 0);

            routeStats.put("status_codes", new LinkedHashMap<>(routeStatusCodes));
            result.put(route, routeStats);
        }

        return result;
    }

    public Map<String, Object> getAggregatedData() {
        return computeAggregates();
    }
}
