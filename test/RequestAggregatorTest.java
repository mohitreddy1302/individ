import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestAggregatorTest {

    @Test
    public void computesResponseTimeStatsAndStatusBucketsPerRoute() {
        RequestAggregator aggregator = new RequestAggregator();

        // Response times for a single route
        aggregator.addResponseTime("/api/update", 100);
        aggregator.addResponseTime("/api/update", 200);
        aggregator.addResponseTime("/api/update", 300);
        aggregator.addResponseTime("/api/update", 400);

        // Status codes for the same route
        aggregator.addStatusCode("/api/update", 201); // 2XX
        aggregator.addStatusCode("/api/update", 404); // 4XX
        aggregator.addStatusCode("/api/update", 500); // 5XX
        aggregator.addStatusCode("/api/update", 503); // 5XX

        Map<String, Object> result = aggregator.getAggregatedData();
        assertTrue(result.containsKey("/api/update"));

        @SuppressWarnings("unchecked")
        Map<String, Object> route = (Map<String, Object>) result.get("/api/update");

        @SuppressWarnings("unchecked")
        Map<String, Object> times = (Map<String, Object>) route.get("response_times");
        assertEquals(100.0, (double) times.get("min"), 0.001);
        assertEquals(400.0, (double) times.get("max"), 0.001);
        assertEquals(250.0, (double) times.get("50_percentile"), 0.001);
        assertEquals(370.0, (double) times.get("90_percentile"), 0.001);
        assertEquals(385.0, (double) times.get("95_percentile"), 0.001);
        assertEquals(397.0, (double) times.get("99_percentile"), 0.001);

        @SuppressWarnings("unchecked")
        Map<String, Integer> codes = (Map<String, Integer>) route.get("status_codes");
        assertEquals(1, (int) codes.get("2XX"));
        assertEquals(1, (int) codes.get("4XX"));
        assertEquals(2, (int) codes.get("5XX"));
    }
}


