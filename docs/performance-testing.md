# Performance Testing

TEAMMATES makes use of [JMeter](https://jmeter.apache.org/) for performance testing.
The relevant files are located in the `src/jmeter/` directory. The JMeter tests are the `.jmx` files located in the `src/jmeter/tests/` directory.

TEAMMATES uses the [`jmeter-gradle-plugin`](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin) for automating the processes of running performance tests and generating reports.

## Running Performance Tests

Start the backend server (`localhost:8080`) before executing the JMeter performance tests.

### Using Gradle

To run the JMeter tests, execute this command from the main project directory:
```
./gradlew clean jmRun
```

If it runs without any errors:
- A log file is generated as `build/jmeter/jmeter.log`.
- The JMeter test results are stored as XML files with the same name as the test file `build/jmeter-report/TEST_FILE_NAME.jmx-TIMESTAMP.xml`. 

> Note: If you want to retain the result files, do not include the `clean` task when running the command.
<br/>

If you want to execute specific test file(s):
- Navigate to the `// PERFORMANCE TEST TASKS` section in `build.gradle`. 
- Under the `jmeter` configuration:
  - Uncomment the `jmTestFiles` parameter.
  - Replace the dummy values with the path to the test file(s) that you want to run.
- Then, follow the same steps mentioned above.

### Using the Command Line

If you have JMeter installed, you can use `jmeter` in the command line to run tests and generate reports.

Execute this command after replacing the parameters with paths to the relevant files:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.csv -j LOG_FILE.log
```

### Using JMeter GUI

You can also use the JMeter GUI (`./gradlew jmGui`) to run performance tests. This is particularly useful when debugging or validating that the test does what is expected.
However, you should not use the GUI to run large scale tests as it is very resource intensive.

## Generating Test Reports

It is possible to generate HTML summary reports from the JMeter test results.

### Using Gradle

Navigate to the `jmeter` section in the `build.gradle` file. Change the `csvLogFile` property to `false`.
Execute this command from the main project directory:
```
./gradlew clean jmRun jmReport
```
The HTML reports are generated as `build/jmeter-report/TEST_FILE_NAME.jmx-TIMESTAMP.html` and can be viewed in a browser.

### Using the Command Line

From the command line, you can use this command to generate a more comprehensive summary report with graphs from the results file that was generated earlier:
```
jmeter -g PATH_TO_RESULT_FILE.csv -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```

If you want to execute the tests and generate the reports together, use:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.csv -e -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```
