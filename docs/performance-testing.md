# Performance Testing

TEAMMATES makes use of [JMeter](https://jmeter.apache.org/) for performance testing, and [`jmeter-gradle-plugin`](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin) for automating the processes of running performance tests and generating reports.
The relevant files are located in the `src/jmeter/` directory. The JMeter tests are the `.jmx` files located in the `src/jmeter/tests/` directory.

## Performance Test Workflow

Before running a performance test, you need to setup the relevant data in the datastore. 
To facilitate this, there are Java scripts in the `teammates.performance.scripts` package.

There are 3 steps involved in doing a performance test:

1. Setting up the data: This is done by executing the relevant Java script in the `setup` package (e.g. `SetupStudentProfileTest`).
    Executing the script will:
    1. Create the relevant TEAMMATES JSON data for the datastore. It is stored as `src/jmeter/resources/data/testName.json`.
    1. Create the relevant CSV Config data for the JMeter test plan. It is stored as `src/jmeter/resources/data/testNameConfig.csv`.
    1. Create the entities in the datastore using the JSON data. This can be verified by checking your [local datastore](http://localhost:8080/_ah/admin/datastore).
        > The backend server (`localhost:8080`) must be started for this to happen successfully.
1. [Run the performance test](#running-performance-tests) specified in the JMeter `.jmx` file in the `src/jmeter/tests/` directory.
1. Remove the data that was added to the datastore (optional, but recommended). To do so, execute the relevant Java script in the `teardown` package (e.g. `DeleteStudentProfileTestData`).
   In addition, you can choose to delete the `.json` and `.csv` files that were generated in step 1. 

## Running Performance Tests

Start the backend server (`localhost:8080`) before executing the JMeter performance tests.

### Using Gradle

To run the JMeter tests, execute this command from the main project directory:
```
./gradlew clean jmRun
```

If you want to execute specific test file(s):
- Navigate to the `jmeter` configuration in `build.gradle`:
  - Uncomment the `jmTestFiles` parameter.
  - Replace the dummy values with the path to the test file(s) that you want to run.
- Then, follow the same steps mentioned above.

If the tests run without any errors:
- A log file is generated as `build/jmeter/jmeter.log`.
- The JMeter test results are stored as CSV files (by default) with the same name as the test file `build/jmeter-report/TEST_FILE_NAME.jmx-TIMESTAMP.csv`. 

> Note: If you want to retain the result files, do not include the `clean` task when running the command.
<br/>

If the build failed:
- Check the `jmeter.log` file to see if there were any errors or exceptions thrown. If so, take the necessary steps to fix them.
- Check the generated CSV result files to see whether the `success` value of any row is `false`. If so, make sure that you have [set up the data](#performance-test-workflow) and take any other necessary steps to resolve it.

### Using the Command Line

If you have JMeter installed, you can use `jmeter` in the command line to run tests and generate reports.

Execute this command after replacing the parameters with paths to the relevant files:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.csv -j LOG_FILE.log
```

### Using JMeter GUI

You can also use the JMeter GUI (`./gradlew jmGui`) to run performance tests. This is particularly useful when debugging or validating that the test does what is expected.
However, you should not use the GUI to run large scale tests as it is very resource intensive.

> Remember to **disable or remove all `Listeners`** in the `.jmx` file, unless you are debugging. Having them enabled can have a negative impact on the test performance.

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

From the command line, you can use this command to generate a more **comprehensive summary report** with graphs from the results file that was generated earlier:
```
jmeter -g PATH_TO_RESULT_FILE.csv -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```

If you want to execute the tests and generate the reports together, use:
```
jmeter -n -t PATH_TO_TEST_FILE.jmx -l RESULT_FILE.csv -e -o REPORT_OUTPUT_FOLDER -j LOG_FILE.log
```
