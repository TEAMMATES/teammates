# Performance Testing

TEAMMATES makes use of [JMeter](https://jmeter.apache.org/) for load and performance (L&P) testing, and uses the [`JMeter Java API`](https://jmeter.apache.org/api/index.html) and TestNG for automating the process of running performance tests and generating reports.
The performance test cases are located in the `teammates.lnp` package. The actual JMeter tests are the `.jmx` files located in the `src/test/lnpTests/` directory.

## Running Performance Tests

[Download JMeter](https://jmeter.apache.org/download_jmeter.cgi) and update the JMeter properties in `src/test/resources/test.properties` accordingly.  
Start the backend server, i.e. `localhost:8080`, before running the performance tests.

### Using Gradle

To run the performance tests, execute this command from the main project directory:
```
./gradlew lnpTests
```

- The JMeter test results are stored as JTL files with the same name as the test file in `src/test/lnpResults/TEST_NAME.jmx.jtl`. 
- A log file is generated as `jmeter.log`.

If the build fails:
- Check the console log messages and the `jmeter.log` file to see if there were any errors or exceptions thrown. If so, take the necessary steps to fix them.
- Check the generated `.jtl` result files to see whether the `success` value of any row is `false`. If so, investigate the cause (possibly by making use of the JMeter GUI and Listeners) and fix it.

### Using the Command Line

If you have JMeter installed, you can use `jmeter` in the command line to run tests and generate reports.

Execute this command after replacing the parameters with the relevant paths:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.jtl -j LOG_FILE.log
```

Also, you can use this command to generate a HTML **summary report** from the results file (`.jtl`) that was generated earlier:
```
jmeter -g PATH_TO_RESULT_FILE.csv -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```

If you want to do both (i.e. execute the tests and generate the reports) together, use:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.csv -e -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```

### Using JMeter GUI

You can also install JMeter and use its GUI to run performance tests. This is particularly useful when debugging or validating that the test works as expected.
However, you should not use the GUI to run large scale tests as it is very resource intensive.

> Remember to **disable or remove all `Listeners`** in the `.jmx` file, unless you are debugging. Having them enabled can have a negative impact on the test performance.
