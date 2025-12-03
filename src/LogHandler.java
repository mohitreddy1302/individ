import java.util.Map;

public interface LogHandler {
    void setNext(LogHandler handler);
    void handle(Map<String, String> logEntry);
}