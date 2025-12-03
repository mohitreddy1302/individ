import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.*;

public class ApplicationLogHandlerTest {
    private ApplicationAggregator aggregator;
    private ApplicationLogHandler handler;

    @Before
    public void setup() {
        aggregator = new ApplicationAggregator();
        handler = new ApplicationLogHandler(aggregator);
    }

    @Test
    public void testHandleValidApplicationLog() {
        Map<String, String> logEntry = new HashMap<>();
        logEntry.put("level", "INFO");
        logEntry.put("message", "User updated profile");
        
        handler.handle(logEntry);
        
        Map<String, Object> result = aggregator.getAggregatedData();
        assertEquals(1, result.get("INFO"));
    }
}