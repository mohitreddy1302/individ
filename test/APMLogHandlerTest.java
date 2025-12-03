import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.*;

public class APMLogHandlerTest {
    private APMAggregator aggregator;
    private APMLogHandler handler;

    @Before
    public void setup() {
        aggregator = new APMAggregator();
        handler = new APMLogHandler(aggregator);
    }

    @Test
    public void testHandleValidAPMLog() {
        Map<String, String> logEntry = new HashMap<>();
        logEntry.put("metric", "cpu_usage_percent");
        logEntry.put("value", "72");
        
        handler.handle(logEntry);
        
        Map<String, Object> result = aggregator.getAggregatedData();
        assertTrue(result.containsKey("cpu_usage_percent"));
    }
    @Test
    public void testHandleInvalidAPMLogMissingFields() {
        Map<String, String> logEntry = new HashMap<>();
        // Missing "value" field
        logEntry.put("metric", "cpu_usage_percent");
        
        handler.handle(logEntry);  // Should not add anything to aggregator
        
        Map<String, Object> result = aggregator.getAggregatedData();
        assertFalse(result.containsKey("cpu_usage_percent"));
    }
}