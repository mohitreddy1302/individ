import java.util.Map;

public abstract class BaseLogHandler implements LogHandler {
    protected LogHandler nextHandler;

    @Override
    public void setNext(LogHandler handler) {
        this.nextHandler = handler;
    }

    protected void handleNext(Map<String, String> logEntry) {
        if (nextHandler != null) {
            nextHandler.handle(logEntry);
        }
    }
}