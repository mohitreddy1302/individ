import java.util.*;

public interface ILogParser {
    Iterator<Map<String, String>> parse(String dataSource);
}