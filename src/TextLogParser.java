import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class TextLogParser implements ILogParser {
    @Override
    public Iterator<Map<String, String>> parse(String dataSource) {
        return new LogFileIterator(dataSource);
    }

    private static final class LogFileIterator implements Iterator<Map<String, String>> {
        private final BufferedReader reader;
        private final String dataSource;
        private String nextLine;

        LogFileIterator(String dataSource) {
            this.dataSource = dataSource;
            try {
                this.reader = new BufferedReader(new FileReader(dataSource));
                this.nextLine = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read log file: " + dataSource, e);
            }
        }

        @Override
        public boolean hasNext() {
            return nextLine != null;
        }

        @Override
        public Map<String, String> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String currentLine = nextLine;
            advance();
            return parseLine(currentLine);
        }

        private void advance() {
            try {
                nextLine = reader.readLine();
                if (nextLine == null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading log file: " + dataSource, e);
            }
        }

        private Map<String, String> parseLine(String line) {
            Map<String, String> logEntry = new HashMap<>();
            if (line == null || line.isBlank()) {
                return logEntry;
            }

            String[] parts = line.split(" ");
            for (String part : parts) {
                String[] keyValue = part.split("=", 2);
                if (keyValue.length == 2) {
                    String value = keyValue[1].replaceAll("^\"|\"$", "");
                    logEntry.put(keyValue[0], value);
                }
            }
            return logEntry;
        }
    }
}
