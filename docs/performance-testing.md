# Performance Testing

TEAMMATES makes use of [JMeter](https://jmeter.apache.org/) for load and performance (L&P) testing, and uses the [JMeter Java API](https://jmeter.apache.org/api/index.html) and TestNG to automate the process of running performance tests.
The performance test cases are located in the `teammates.e2e.cases.lnp` package.

## Creating Performance Tests

Each new test case must inherit `BaseLNPTestCase`, create a `LNPTestData` instance, and implement all the `abstract` methods from both classes. 
The L&P test plans are created using the JMeter API in the overridden `generateTestPlan()` method, which is invoked by `BaseLNPTestCase#getLNPTestPlan(boolean)`. 
This boolean parameter can be set to `true` in the method call to generate the equivalent `.jmx` file, which can be opened in the JMeter GUI.
By default, the file is saved as `baseLnpTest.jmx` in the root directory of your TEAMMATES project.

To see a sample implementation of a test case, you can refer to `StudentProfileLNPTest`. It is a _simple_ test case which load tests a GET endpoint (`/webapi/student/profile`).

## Running Performance Tests

[Download JMeter](https://jmeter.apache.org/download_jmeter.cgi) and update the JMeter properties in `src/e2e/resources/test.properties` accordingly.  
Start the backend server, i.e. `localhost:8080`, before running the performance tests.

### Using Gradle

To run the performance tests, execute this command from the main project directory:
```sh
./gradlew lnpTests
```

- The JMeter test results are stored as JTL files with the same name as the test file in `src/e2e/lnp/results/TEST_NAME.jmx.jtl`. 
- A log file is generated as `jmeter.log`.

If the build fails:
- Check the console log messages and the `jmeter.log` file to see if there were any errors or exceptions thrown. If so, take the necessary steps to fix them.
- Check the generated `.jtl` result files to see whether the `success` value of any row is `false`. If so, investigate the cause (possibly by making use of the JMeter GUI and Listeners) and fix it.

### Using the Command Line

If you have JMeter installed, you can use `jmeter` in the command line to run tests and generate reports.

Execute this command after replacing the parameters with the relevant paths:
```sh
jmeter -n -t PATH_TO_TEST_FILE.jmx -l PATH_TO_RESULT_FILE.jtl -j PATH_TO_LOG_FILE.log
```

Also, you can use this command to generate a HTML **summary report** from the results file (`.jtl`) that was generated earlier:
```sh
jmeter -g PATH_TO_RESULT_FILE.jtl -o REPORT_OUTPUT_FOLDER -j PATH_TO_LOG_FILE.log
```

If you want to do both together (i.e. execute the test and generate the report), use:
```sh
jmeter -n -t PATH_TO_TEST_FILE.jmx -l PATH_TO_RESULT_FILE.jtl -e -o REPORT_OUTPUT_FOLDER -j PATH_TO_LOG_FILE.log
```

### Using JMeter GUI

You can also install JMeter and use its GUI to run performance tests. This is particularly useful when debugging or validating that the test works as expected.
However, you should not use the GUI to run large scale tests as it is very resource intensive.

> Remember to **disable or remove all `Listeners`** in the `.jmx` file, unless you are debugging. Having them enabled can have a negative impact on the test performance.
