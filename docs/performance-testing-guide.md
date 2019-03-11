# Performance Testing


## Running Performance Tests

Ensure that you have installed JMeter and set it as an environment variable.
After doing so, follow these steps to execute a test:

1. Navigate to the `jmeter/` directory.
1. Run the command below after replacing `<path-to-test-file>` with a path to a JMeter test (`.jmx` extension), e.g., `testTemplate.jmx`.
    ```$xslt
    ./runTest.sh <path-to-test-file>
    ```
1. The test result will be saved as `<test-name>_result.csv` file in the `jmeter/results` directory. 