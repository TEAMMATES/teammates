# Performance Testing

TEAMMATES makes use of [JMeter](https://jmeter.apache.org/) for performance testing.
The JMeter tests are the `.jmx` files located in `src/test/jmeter/tests/`.

## Running Performance Tests

1. Navigate to the `src/test/jmeter/` directory.
1. Run the command below after replacing `<path-to-test-file>` with a path to a JMeter test (`.jmx` extension), e.g., `testTemplate.jmx`.
    ```$xslt
    ./runTest.sh <path-to-test-file>
    ```
1. The test result will be saved as `<test-name>_result.jtl` in the `src/test/jmeter/results` directory. 

## Generating test reports

### Using [`jmeter-gradle-plugin`](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin).

To generate a test report:

- Execute the command:
   ```
   ./gradlew jmRun jmReport
   ```
- The `jmeter.log` file is generated in the `build/jmeter` directory.
- The raw test results are in `build/jmeter-report/loadtest-results.xml`. 
- The HTML report is generated as `build/jmeter-report/loadtest-results.html`.

**Note:** By default, the test result and report file are only generated for the last test file (alphabetically) in the JMeter test directory.

If you want to generate the report for a specific file:
- Navigate to the `// PERFORMANCE TEST TASKS` section in `build.gradle`. 
- Under the `jmeter` configuration:
  - Uncomment the `jmTestFiles` line.
  - Replace `testName.jmx` with the the name of the test file that you want the report for.
- Then follow the same steps mentioned above.
