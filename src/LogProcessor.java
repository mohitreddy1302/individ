import java.util.*;

public class LogProcessor {
    private ILogParser parser;
    private final List<LogHandler> handlers = new ArrayList<>();
    private final FileWriterStrategy fileWriter;

    public LogProcessor(FileWriterStrategy fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void setParser(ILogParser parser) {
        this.parser = parser;
    }

    public void addHandler(LogHandler handler) {
        if (!handlers.isEmpty()) {
            handlers.get(handlers.size() - 1).setNext(handler);
        }
        handlers.add(handler);
    }

    public void processFile(String filename) {
        if (parser == null) {
            throw new IllegalStateException("Parser not set");
        }

        Iterator<Map<String, String>> logEntries = parser.parse(filename);
        while (logEntries.hasNext()) {
            Map<String, String> logEntry = logEntries.next();
            if (!handlers.isEmpty()) {
                handlers.get(0).handle(logEntry);
            }
        }

        // Call the file writer after processing all logs
        //fileWriter.writeFiles(new HashMap<>(), "json"); // Empty data for testing purposes
        boolean testingMode = false; // Set this to true only when testing
        if (testingMode) {
        fileWriter.writeFiles(new HashMap<>(),"json");}
        
    }

    // Method to get the file writer (for testing)
    public FileWriterStrategy getFileWriter() {
        return fileWriter;
    }
}
