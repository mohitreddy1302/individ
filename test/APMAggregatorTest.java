import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class APMAggregatorTest {

    @Test
    public void computesMinMedianAverageAndMaxForOddCount() {
        APMAggregator aggregator = new APMAggregator();
        aggregator.addData("cpu_usage_percent", 60);
        aggregator.addData("cpu_usage_percent", 70);
        aggregator.addData("cpu_usage_percent", 90);

        Map<String, Object> result = aggregator.getAggregatedData();
        assertTrue(result.containsKey("cpu_usage_percent"));

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.get("cpu_usage_percent");

        assertEquals(60.0, (double) stats.get("minimum"), 0.0001);
        assertEquals(70.0, (double) stats.get("median"), 0.0001);
        assertEquals(73.3333, (double) stats.get("average"), 0.0001);
        assertEquals(90.0, (double) stats.get("max"), 0.0001);
    }

    @Test
    public void computesMedianCorrectlyForEvenCount() {
        APMAggregator aggregator = new APMAggregator();
        aggregator.addData("cpu_usage_percent", 60);
        aggregator.addData("cpu_usage_percent", 80);

        Map<String, Object> result = aggregator.getAggregatedData();

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.get("cpu_usage_percent");

        assertEquals(70.0, (double) stats.get("median"), 0.0001);
    }
}


