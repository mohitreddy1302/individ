import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JsonFileWriter implements FileWriterStrategy {
    private static final String JSON_EXTENSION = ".json";

    private final Gson gson;

    public JsonFileWriter() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    }

    @Override
    public void writeFiles(Map<String, Object> aggregatedData, String fileType) {
        Map<String, Object> safeData = aggregatedData == null ? Map.of() : aggregatedData;
        String filename = buildFilename(Objects.requireNonNull(fileType, "fileType"));
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(safeData, writer);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            throw new RuntimeException("Failed to write to " + filename, e);
        }
    }

    private String buildFilename(String fileType) {
        return fileType + JSON_EXTENSION;
    }
}
