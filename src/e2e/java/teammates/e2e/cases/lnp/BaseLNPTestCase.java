package teammates.e2e.cases.lnp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
import teammates.e2e.util.LNPTestData;
import teammates.e2e.util.TestProperties;
import teammates.test.cases.BaseTestCase;

/**
 * Base class for all L&P test cases.
 */
public abstract class BaseLNPTestCase extends BaseTestCase {

    private static final Logger log = Logger.getLogger();

    /**
     * Returns the path to the generated JSON data bundle file.
     */
    protected abstract String getJsonDataPath();

    /**
     * Returns the path to the generated JMeter CSV config file.
     */
    protected abstract String getCsvConfigPath();

    @Override
    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    private String getPathToFile(String fileName) {
        return TestProperties.TEST_DATA_FOLDER + fileName;
    }

    /**
     * Creates the JSON data and writes it to the file specified by {@link #getJsonDataPath()}.
     */
    private void createJsonDataFile(LNPTestData testData) throws IOException {
        DataBundle jsonData = testData.generateJsonData();
        String outputJsonPath = getJsonDataPath();

        String pathToResultFile = getPathToFile(outputJsonPath);
        File file = new File(pathToResultFile);

        // Write data to the file; overwrite if it already exists
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

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
     * Writes the data to the CSV file specified by {@code pathToResultFileParam}.
     */
    private void writeDataToCsvFile(List<String> headers, List<List<String>> valuesList, String pathToResultFileParam)
            throws IOException {
        String pathToResultFile = getPathToFile(pathToResultFileParam);
        File file = new File(pathToResultFile);

        // Write data to the file; overwrite if it already exists
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
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
     * Generates the JMeter LNP test plan.
     * @return A nested tree structure that consists of the various elements that are used in the JMeter test.
     */
    protected abstract HashTree generateTestPlan();

    /**
     * Returns the generated LNP test plan.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     * @return A nested tree structure that consists of the various elements that are used in the JMeter test.
     * @throws IOException if there is an error when saving the test to a file.
     */
    private HashTree getLnpTestPlan(boolean shouldCreateJmxFile) throws IOException {
        HashTree testPlanHashTree = generateTestPlan();

        if (shouldCreateJmxFile) {
            SaveService.saveTree(testPlanHashTree, Files.newOutputStream(Paths.get(this.toString() + ".jmx")));
        }

        return testPlanHashTree;
    }

    /**
     * Creates the JSON test data and CSV config data files for the performance test from {@code testData}.
     */
    protected void createTestData(LNPTestData testData) {
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
     * Runs the JMeter test specified by {@code jmxFile}.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     */
    protected void runJmeter(boolean shouldCreateJmxFile) throws Exception {
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

        String resultFile = TestProperties.LNP_TEST_RESULTS_FOLDER + this.toString() + "-results.jtl";
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

        // TODO: Generate summary report from .jtl results file.
    }

    /**
     * Deletes the JSON and CSV data files that were created.
     */
    protected void deleteDataFiles() throws IOException {
        String pathToJsonFile = getPathToFile(getJsonDataPath());
        String pathToCsvFile = getPathToFile(getCsvConfigPath());

        Files.delete(Paths.get(pathToJsonFile));
        Files.delete(Paths.get(pathToCsvFile));
    }

    @Override
    public String toString() {
        return "baseLnpTest";
    }

}
