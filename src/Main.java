
public class Main {
    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("--file")) {
            System.out.println("Usage: java -jar logprocessor.jar --file <filename.txt>");
            System.exit(1);
        }

        String filename = args[1];
        
        // Create aggregators
        APMAggregator apmAggregator = new APMAggregator();
        ApplicationAggregator applicationAggregator = new ApplicationAggregator();
        RequestAggregator requestAggregator = new RequestAggregator();

        // Create handlers
        LogHandler apmHandler = new APMLogHandler(apmAggregator);
        LogHandler applicationHandler = new ApplicationLogHandler(applicationAggregator);
        LogHandler requestHandler = new RequestLogHandler(requestAggregator);

        // Create processor
        LogProcessor processor = new LogProcessor(new JsonFileWriter());
        processor.setParser(new TextLogParser());

        // Add handlers
        processor.addHandler(apmHandler);
        processor.addHandler(applicationHandler);
        processor.addHandler(requestHandler);

        // Process file
        processor.processFile(filename);

        // Write results
        JsonFileWriter writer = new JsonFileWriter();
        writer.writeFiles(apmAggregator.getAggregatedData(), "output/apm");
        writer.writeFiles(applicationAggregator.getAggregatedData(), "output/application");
        writer.writeFiles(requestAggregator.getAggregatedData(), "output/request");
        
    }
}