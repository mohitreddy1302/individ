import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApplicationAggregatorTest {

    @Test
    public void countsLogsPerLevel() {
        ApplicationAggregator aggregator = new ApplicationAggregator();
        aggregator.addData("INFO");
        aggregator.addData("INFO");
        aggregator.addData("ERROR");
        aggregator.addData("DEBUG");

        Map<String, Object> result = aggregator.getAggregatedData();

        assertEquals(2, result.get("INFO"));
        assertEquals(1, result.get("ERROR"));
        assertEquals(1, result.get("DEBUG"));
    }
}


