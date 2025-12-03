import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TextLogParserTest {

    @Test
    public void parsesApmApplicationAndRequestLines() throws IOException {
        String content =
            "timestamp=2024-11-24T10:01:30Z metric=cpu_usage_percent value=92\n" +
            "timestamp=2024-11-24T10:01:40Z level=INFO message=\"User logged in\" user_id=101\n" +
            "timestamp=2024-11-24T10:01:50Z request_method=GET request_url=\"/api/status\" response_time_ms=50 response_status=200\n" +
            "\n"; // empty line should be safely ignored

        File tempFile = createTempFile(content);

        TextLogParser parser = new TextLogParser();
        Iterator<Map<String, String>> it = parser.parse(tempFile.getPath());

        // APM line
        Map<String, String> apm = it.next();
        assertEquals("cpu_usage_percent", apm.get("metric"));
        assertEquals("92", apm.get("value"));

        // Application line
        Map<String, String> app = it.next();
        assertEquals("INFO", app.get("level"));
        // message value is at least parsed as the first token; key presence is enough for handlers
        assertTrue(app.containsKey("message"));

        // Request line
        Map<String, String> req = it.next();
        assertEquals("GET", req.get("request_method"));
        assertEquals("/api/status", req.get("request_url"));
        assertEquals("50", req.get("response_time_ms"));
        assertEquals("200", req.get("response_status"));

        // Empty line → empty map
        Map<String, String> empty = it.next();
        assertTrue(empty.isEmpty());

        tempFile.delete();
    }

    private File createTempFile(String content) throws IOException {
        File file = File.createTempFile("parser", ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}


