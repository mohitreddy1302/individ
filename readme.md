# Log Processor

## Compile & Run the Application

```bash
# 1. Compile all sources into bin/, including every JAR in lib/
javac -cp "lib/*:bin" -d bin src/*.java

# 2. Run the program against your sample log
sudo java -cp "bin:lib/*" Main --file src/sample_input_logs-1.txt
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
     RequestLogHandlerTest

