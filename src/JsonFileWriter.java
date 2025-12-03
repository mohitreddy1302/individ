import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.Map;

public class JsonFileWriter implements FileWriterStrategy {
    private final Gson gson;

    public JsonFileWriter() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    }

    @Override
    public void writeFiles(Map<String, Object> aggregatedData, String fileType) {
        if (aggregatedData == null) {
            aggregatedData = Map.of();
        }

        String filename = fileType + ".json";
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(aggregatedData, writer);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            throw new RuntimeException("Failed to write to " + filename, e);
        }
    }
}