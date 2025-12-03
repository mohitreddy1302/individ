import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class LogProcessorTest {

    private LogProcessor logProcessor;
    private TextLogParser textLogParser;
    private APMLogHandler apmHandler;
    private ApplicationLogHandler appHandler;
    private RequestLogHandler requestHandler;
    private JsonFileWriter jsonWriter;
    private APMAggregator apmAggregator;
    private ApplicationAggregator appAggregator;
    private RequestAggregator requestAggregator;

    @Before
    public void setUp() {
        textLogParser = new TextLogParser();
        jsonWriter = new JsonFileWriter();

        apmAggregator = new APMAggregator();
        appAggregator = new ApplicationAggregator();
        requestAggregator = new RequestAggregator();

        logProcessor = new LogProcessor(jsonWriter);

        apmHandler = new APMLogHandler(apmAggregator);
        appHandler = new ApplicationLogHandler(appAggregator);
        requestHandler = new RequestLogHandler(requestAggregator);

        apmHandler.setNext(appHandler);
        appHandler.setNext(requestHandler);

        logProcessor.setParser(textLogParser);
        logProcessor.addHandler(apmHandler);
        logProcessor.addHandler(appHandler);
        logProcessor.addHandler(requestHandler);
    }

    @Test
    public void testProcessLogsWithAllLogTypes() throws IOException {
        String testLogData = 
            "timestamp=2024-11-24T10:01:30Z metric=cpu_usage_percent value=92\n" +
            "timestamp=2024-11-24T10:01:40Z level=INFO message=\"User logged in\" user_id=101\n" +
            "timestamp=2024-11-24T10:01:50Z request_method=GET request_url=\"/api/status\" response_time_ms=50 response_status=200\n";
    
        File tempFile = createTempFileWithContent(testLogData);
    
        // Process the log file
        logProcessor.processFile(tempFile.getPath());
    
        // Write out the files explicitly here
        jsonWriter.writeFiles(apmAggregator.getAggregatedData(), "apm");
        jsonWriter.writeFiles(appAggregator.getAggregatedData(), "application");
        jsonWriter.writeFiles(requestAggregator.getAggregatedData(), "request");
    
        // Now the files should exist
        assertTrue("APM JSON file should be created", new File("apm.json").exists());
        assertTrue("Application JSON file should be created", new File("application.json").exists());
        assertTrue("Request JSON file should be created", new File("request.json").exists());
    
        // Clean up
        tempFile.delete();
        new File("apm.json").delete();
        new File("application.json").delete();
        new File("request.json").delete();
    }
    

    @Test
    public void testHandleMultipleAPMLogs() {
        // Create multiple APM log entries
        Map<String, String> logEntry1 = new HashMap<>();
        logEntry1.put("metric", "cpu_usage_percent");
        logEntry1.put("value", "85");

        Map<String, String> logEntry2 = new HashMap<>();
        logEntry2.put("metric", "cpu_usage_percent");
        logEntry2.put("value", "90");

        apmHandler.handle(logEntry1);
        apmHandler.handle(logEntry2);

        Map<String, Object> aggregatedData = apmAggregator.getAggregatedData();
        // Only one metric key, since both values are for "cpu_usage_percent"
        assertEquals("APM Aggregator should contain 1 entry for cpu_usage_percent", 1, aggregatedData.size());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cpuData = (Map<String, Object>) aggregatedData.get("cpu_usage_percent");
        double avg = (double) cpuData.get("average");
        // The average of 85 and 90 is 87.5, so this confirms both values were processed.
        assertEquals("Average should reflect both data points", 87.5, avg, 0.001);
        
    }

    @Test
    public void testHandleApplicationLogWithSeverity() {
        Map<String, String> logEntry = new HashMap<>();
        logEntry.put("level", "INFO");
        logEntry.put("message", "User logged out");

        appHandler.handle(logEntry);

        // Validate that the Application aggregator processed the log correctly
        Map<String, Object> aggregatedData = appAggregator.getAggregatedData();
        assertTrue("Application aggregator should have severity counts", aggregatedData.size() > 0);
        assertTrue("Application log should be counted in severity counts", aggregatedData.containsKey("INFO"));
    }

    @Test
    public void testHandleRequestLogWithMultipleStatusCodes() {
        // Create multiple request log entries
        Map<String, String> logEntry1 = new HashMap<>();
        logEntry1.put("request_method", "POST");
        logEntry1.put("request_url", "/api/update");
        logEntry1.put("response_time_ms", "120");
        logEntry1.put("response_status", "200"); // changed key
        
        Map<String, String> logEntry2 = new HashMap<>();
        logEntry2.put("request_method", "GET");
        logEntry2.put("request_url", "/api/status");
        logEntry2.put("response_time_ms", "80");
        logEntry2.put("response_status", "404"); // changed key
        

        requestHandler.handle(logEntry1);
        requestHandler.handle(logEntry2);

        // Validate that the Request aggregator processed the logs correctly
        Map<String, Object> aggregatedData = requestAggregator.getAggregatedData();
        assertTrue("Request aggregator should have response times", aggregatedData.size() > 0);
        assertTrue("Request log should be added to the aggregator", aggregatedData.containsKey("/api/update"));
        assertTrue("Request log should be added to the aggregator", aggregatedData.containsKey("/api/status"));
    }

    @Test
    public void testHandleEmptyLog() {
        // Test with an empty log entry
        Map<String, String> emptyLogEntry = new HashMap<>();
        apmHandler.handle(emptyLogEntry);
        appHandler.handle(emptyLogEntry);
        requestHandler.handle(emptyLogEntry);

        // Validate that no data is added to the aggregator for empty log entries
        assertTrue("APM aggregator should be empty for empty log", apmAggregator.getAggregatedData().isEmpty());
        assertTrue("Application aggregator should be empty for empty log", appAggregator.getAggregatedData().isEmpty());
        assertTrue("Request aggregator should be empty for empty log", requestAggregator.getAggregatedData().isEmpty());
    }

    private File createTempFileWithContent(String content) throws IOException {
        File tempFile = File.createTempFile("testLog", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        return tempFile;
    }
}
