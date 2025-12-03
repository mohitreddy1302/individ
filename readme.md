# Log Processor

## Compile & Run the Application

```bash
# 1. Compile all sources into bin/, including every JAR in lib/
javac -cp "lib/*:bin" -d bin src/*.java

# 2. Run the program against your sample log (no sudo needed)
java -cp "bin:lib/*" Main --file src/sample_input_logs-1.txt

# 3. After running, the following JSON files will be created in the current directory:
#    - apm.json
#    - application.json
#    - request.json
```

## Test the Application

```bash
# 1. (Re)compile application code
javac -cp "lib/*:bin" -d bin src/*.java

# 2. Compile the test classes
javac -cp "lib/*:bin" -d bin test/*.java

# 3. Execute all tests with JUnitCore
java -cp "bin:lib/*" org.junit.runner.JUnitCore \
     APMLogHandlerTest \
     ApplicationLogHandlerTest \
     LogProcessorTest \
     RequestLogHandlerTest \
     TextLogParserTest \
     APMAggregatorTest \
     ApplicationAggregatorTest \
     RequestAggregatorTest
```

## Notes

- Running the JUnit tests will temporarily create `apm.json`, `application.json`, and `request.json` and then **delete them as part of test cleanup**.
- To regenerate the final JSON output files for inspection or submission, run:

```bash
java -cp "bin:lib/*" Main --file src/sample_input_logs-1.txt
```


