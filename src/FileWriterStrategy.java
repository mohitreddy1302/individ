import java.util.Map;

public interface FileWriterStrategy {
    void writeFiles(Map<String, Object> aggregatedData, String fileType);
}