
import java.util.Map;

public class Main {
    private static final String FILE_FLAG = "--file";
    private static final int EXPECTED_ARG_COUNT = 2;

    public static void main(String[] args) {
        String filename = parseArguments(args);

        APMAggregator apmAggregator = new APMAggregator();
        ApplicationAggregator applicationAggregator = new ApplicationAggregator();
        RequestAggregator requestAggregator = new RequestAggregator();

        LogProcessor processor = buildProcessor(apmAggregator, applicationAggregator, requestAggregator);
        processor.setParser(new TextLogParser());
        processor.processFile(filename);

        FileWriterStrategy writer = processor.getFileWriter();
        // Assignment requires these exact JSON filenames: apm.json, application.json, request.json
        writeOutput(writer, apmAggregator.getAggregatedData(), "apm");
        writeOutput(writer, applicationAggregator.getAggregatedData(), "application");
        writeOutput(writer, requestAggregator.getAggregatedData(), "request");
    }

    private static LogProcessor buildProcessor(APMAggregator apmAggregator,
                                               ApplicationAggregator applicationAggregator,
                                               RequestAggregator requestAggregator) {
        LogProcessor processor = new LogProcessor(new JsonFileWriter());
        LogHandler apmHandler = new APMLogHandler(apmAggregator);
        LogHandler applicationHandler = new ApplicationLogHandler(applicationAggregator);
        LogHandler requestHandler = new RequestLogHandler(requestAggregator);

        processor.addHandler(apmHandler);
        processor.addHandler(applicationHandler);
        processor.addHandler(requestHandler);
        return processor;
    }

    private static void writeOutput(FileWriterStrategy writer,
                                    Map<String, Object> aggregateData,
                                    String fileType) {
        writer.writeFiles(aggregateData, fileType);
    }

    private static String parseArguments(String[] args) {
        if (args.length != EXPECTED_ARG_COUNT || !FILE_FLAG.equals(args[0])) {
            System.out.println("Usage: java -jar logprocessor.jar --file <filename.txt>");
            System.exit(1);
        }
        return args[1];
    }
}