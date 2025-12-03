import java.io.*;
import java.util.*;

public class TextLogParser implements ILogParser {
    @Override
    public Iterator<Map<String, String>> parse(String dataSource) {
        return new Iterator<Map<String, String>>() {
            private BufferedReader reader;
            private String nextLine;

            {
                try {
                    // Initialize BufferedReader for file reading
                    reader = new BufferedReader(new FileReader(dataSource));
                    nextLine = reader.readLine();
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
                try {
                    nextLine = reader.readLine();
                    if (nextLine == null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading log file: " + dataSource, e);
                }

                return parseLine(currentLine);
            }

            private Map<String, String> parseLine(String line) {
                Map<String, String> logEntry = new HashMap<>();
                String[] parts = line.split(" ");

                // Handle empty lines gracefully
                if (parts.length == 0) {
                    return logEntry;
                }

                for (String part : parts) {
                    String[] keyValue = part.split("=", 2);
                    if (keyValue.length == 2) {
                        String value = keyValue[1].replaceAll("^\"|\"$", "");
                        logEntry.put(keyValue[0], value);
                    }
                }

                return logEntry;
            }
        };
    }
}
