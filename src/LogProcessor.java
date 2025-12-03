import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LogProcessor {
    private ILogParser parser;
    private final List<LogHandler> handlers = new ArrayList<>();
    private final FileWriterStrategy fileWriter;

    public LogProcessor(FileWriterStrategy fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void setParser(ILogParser parser) {
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    public void addHandler(LogHandler handler) {
        Objects.requireNonNull(handler, "handler");
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
        LogHandler firstHandler = getFirstHandler();
        if (firstHandler == null) {
            return;
        }
        while (logEntries.hasNext()) {
            firstHandler.handle(logEntries.next());
        }
    }

    // Method to get the file writer (for testing)
    public FileWriterStrategy getFileWriter() {
        return fileWriter;
    }

    private LogHandler getFirstHandler() {
        return handlers.isEmpty() ? null : handlers.get(0);
    }
}
