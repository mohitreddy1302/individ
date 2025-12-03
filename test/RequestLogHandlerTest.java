import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.*;

public class RequestLogHandlerTest {
    private RequestAggregator aggregator;
    private RequestLogHandler handler;

    @Before
    public void setup() {
        aggregator = new RequestAggregator();
        handler = new RequestLogHandler(aggregator);
    }

    @Test
    public void testHandleValidRequestLog() {
        Map<String, String> logEntry = new HashMap<>();
        logEntry.put("request_url", "/api/test");
        logEntry.put("response_time_ms", "100");
        logEntry.put("response_status", "200");
        
        handler.handle(logEntry);
        
        Map<String, Object> result = aggregator.getAggregatedData();
        assertTrue(result.containsKey("/api/test"));
    }
}