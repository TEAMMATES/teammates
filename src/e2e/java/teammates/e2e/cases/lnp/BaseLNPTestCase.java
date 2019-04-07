package teammates.e2e.cases.lnp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.TeammatesException;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.JMeterConfig;
import teammates.e2e.util.LNPTestData;
import teammates.e2e.util.TestProperties;
import teammates.test.cases.BaseTestCase;

/**
 * Base class for all L&P test cases.
 */
public abstract class BaseLNPTestCase extends BaseTestCase {

    private static final Logger log = Logger.getLogger();

    protected abstract LNPTestData getTestData();

    /**
     * Returns the path to the generated JSON data bundle file.
     */
    protected abstract String getJsonDataPath();

    /**
     * Returns the path to the generated JMeter CSV config file.
     */
    protected abstract String getCsvConfigPath();

    /**
     * Returns the number of threads (users) in the L&P test.
     */
    protected abstract int getNumberOfThreads();

    /**
     * Returns the ramp-up period (in seconds) for the L&P test.
     */
    protected abstract int getRampUpPeriod();

    /**
     * Returns the API endpoint that is to be L&P tested.
     */
    protected abstract String getTestEndpoint();

    /**
     * Returns the HTTP method for the endpoint.
     */
    protected abstract String getTestMethod();

    /**
     * Returns the parameters and corresponding values used in the HTTP request to the test endpoint.
     */
    protected abstract Map<String, String> getTestParameters();

    @Override
    protected String getTestDataFolder() {
        return TestProperties.LNP_TEST_DATA_FOLDER;
    }

    private String getPathToTestDataFile(String fileName) {
        return TestProperties.LNP_TEST_DATA_FOLDER + fileName;
    }

    private String createFileAndDirectory(String directory, String fileName) throws IOException {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String pathToFile = directory + fileName;
        File file = new File(pathToFile);

        // Write data to the file; overwrite if it already exists
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return pathToFile;
    }

    /**
     * Creates the JSON data and writes it to the file specified by {@link #getJsonDataPath()}.
     */
    private void createJsonDataFile(LNPTestData testData) throws IOException {
        DataBundle jsonData = testData.generateJsonData();
        String outputJsonPath = getJsonDataPath();
        String pathToResultFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, outputJsonPath);

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            bw.write(JsonUtils.toJson(jsonData));
            bw.flush();
        }
    }

    /**
     * Creates the CSV data and writes it to the file specified by {@link #getCsvConfigPath()}.
     */
    private void createCsvConfigDataFile(LNPTestData testData) throws IOException {
        List<String> headers = testData.generateCsvHeaders();
        List<List<String>> data = testData.generateCsvData();

        writeDataToCsvFile(headers, data, getCsvConfigPath());
    }

    /**
     * Writes the data to the CSV file specified by {@code pathToCsvFileParam}.
     */
    private void writeDataToCsvFile(List<String> headers, List<List<String>> valuesList, String pathToCsvFileParam)
            throws IOException {
        String pathToCsvFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, pathToCsvFileParam);

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToCsvFile))) {
            // Write headers and data to the CSV file
            bw.write(convertToCsv(headers));

            for (List<String> values : valuesList) {
                bw.write(convertToCsv(values));
            }

            bw.flush();
        }
    }

    /**
     * Converts the list of {@code values} to a CSV row.
     * @return A single string containing {@code values} separated by pipelines and ending with newline.
     */
    private String convertToCsv(List<String> values) {
        StringJoiner csvRow = new StringJoiner("|", "", "\n");
        for (String value : values) {
            csvRow.add(value);
        }
        return csvRow.toString();
    }

    /**
     * Setup and load the JMeter configuration and property files to run the Jmeter test.
     * @throws IOException if the save service properties file cannot be loaded.
     */
    private void loadJmeterProperties() throws IOException {
        JMeterUtils.loadJMeterProperties(TestProperties.JMETER_PROPERTIES_PATH);
        JMeterUtils.setJMeterHome(TestProperties.JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();
    }

    /**
     * Returns the generated LNP test plan.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     * @return A nested tree structure that consists of the various elements that are used in the JMeter test.
     * @throws IOException if there is an error when saving the test to a file.
     */
    private HashTree getLnpTestPlan(boolean shouldCreateJmxFile) throws IOException {
        String csvConfigPath = getCsvConfigPath();
        int numThreads = getNumberOfThreads();
        int rampUpPeriod = getRampUpPeriod();
        String testEndpoint = getTestEndpoint();
        String testMethod = getTestMethod();
        Map<String, String> args = getTestParameters();

        HashTree testPlanHashTree = new JMeterConfig() {

            @Override
            protected int getNumberOfThreads() {
                return numThreads;
            }

            @Override
            protected int getRampUpPeriod() {
                return rampUpPeriod;
            }

            @Override
            protected String getTestEndpoint() {
                return testEndpoint;
            }

            @Override
            protected String getTestMethod() {
                return testMethod;
            }

            @Override
            protected Map<String, String> getTestArguments() {
                return args;
            }

            @Override
            protected String getCsvConfigPath() {
                return getPathToTestDataFile(csvConfigPath);
            }

        }.createTestPlan();

        if (shouldCreateJmxFile) {
            String pathToConfigFile = createFileAndDirectory(
                    TestProperties.LNP_TEST_CONFIG_FOLDER, "/" + getClass().getSimpleName() + ".jmx");

            SaveService.saveTree(testPlanHashTree, Files.newOutputStream(Paths.get(pathToConfigFile)));
        }

        return testPlanHashTree;
    }

    /**
     * Creates the JSON test data and CSV config data files for the performance test from {@code testData}.
     */
    protected void createTestData() {
        LNPTestData testData = getTestData();
        try {
            createJsonDataFile(testData);
            createCsvConfigDataFile(testData);
        } catch (IOException ex) {
            log.severe(TeammatesException.toStringWithStackTrace(ex));
        }
    }

    /**
     * Creates the entities in the datastore from the file specified by {@code dataBundleJsonPath}.
     *
     * @param dataBundleJsonPath Path to the data bundle to be added
     */
    protected void persistTestData(String dataBundleJsonPath) {
        DataBundle dataBundle = loadDataBundle(dataBundleJsonPath);
        BackDoor.removeAndRestoreDataBundle(dataBundle);
    }

    /**
     * Deletes the data that was created in the datastore from the file specified by {@code dataBundleJsonPath}.
     */
    protected void deleteTestData(String dataBundleJsonPath) {
        DataBundle dataBundle = loadDataBundle(dataBundleJsonPath);
        BackDoor.removeDataBundle(dataBundle);
    }

    /**
     * Runs the JMeter test.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     */
    protected void runJmeter(boolean shouldCreateJmxFile) throws IOException {
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        loadJmeterProperties();

        // Load JMeter Test Plan
        HashTree testPlanTree = getLnpTestPlan(shouldCreateJmxFile);

        // Create summariser for generating results file
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String resultFile = TestProperties.LNP_TEST_RESULTS_FOLDER + "/" + getClass().getSimpleName() + ".jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        File file = new File(resultFile);
        if (file.exists()) {
            file.delete();
        }

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // TODO: As mentioned in the docs, good to fail the test if the `success` value of any row is `false`,
        //  or if there is an Exception.
        //  An example of when this occurs is if `email` is used for logging in instead of `googleid`, or if the JMeter
        //  test properties are not set.
    }

    /**
     * Deletes the JSON and CSV data files that were created.
     */
    protected void deleteDataFiles() throws IOException {
        String pathToJsonFile = getPathToTestDataFile(getJsonDataPath());
        String pathToCsvFile = getPathToTestDataFile(getCsvConfigPath());

        Files.delete(Paths.get(pathToJsonFile));
        Files.delete(Paths.get(pathToCsvFile));
    }

}
