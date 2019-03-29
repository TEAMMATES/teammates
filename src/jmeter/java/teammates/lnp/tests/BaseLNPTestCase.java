package teammates.lnp.tests;

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
import teammates.lnp.util.LNPTestData;
import teammates.lnp.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * Base class for all L&P test cases.
 */
public abstract class BaseLNPTestCase {

    private static final Logger log = Logger.getLogger();

    /**
     * Returns the path to the generated JSON data bundle file.
     */
    protected abstract String getPathToJsonDataFile();

    /**
     * Returns the path to the generated JMeter CSV config file.
     */
    protected abstract String getPathToCsvConfigFile();

    /**
     * Creates the test data folder if it does not exist.
     */
    private static boolean createTestDataFolder() {
        File testDataDirectory = new File(TestProperties.JMETER_TEST_DATA_DIRECTORY);
        if (!testDataDirectory.exists()) {
            return testDataDirectory.mkdir();
        }
        return true;
    }

    /**
     * Creates the JSON data and writes it to the file specified by {@link BaseLNPTestCase#getPathToJsonDataFile()}.
     */
    private void createJsonDataFile(LNPTestData testData) throws IOException {
        if (!createTestDataFolder()) {
            throw new IOException("Test data directory does not exist");
        }

        DataBundle jsonData = testData.generateJsonData();
        String outputJsonPath = getPathToJsonDataFile();

        String pathToResultFile = (outputJsonPath.charAt(0) == '/' ? TestProperties.JMETER_TEST_DATA_DIRECTORY : "")
                + outputJsonPath;
        File file = new File(pathToResultFile);

        // Write data to the file; overwrite if it already exists
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            bw.write(JsonUtils.toJson(jsonData));
            bw.flush();
        }
    }

    /**
     * Creates the CSV data and writes it to the file specified by {@link BaseLNPTestCase#getPathToCsvConfigFile()}.
     */
    private void createCsvConfigDataFile(LNPTestData testData) throws IOException {
        List<String> headers = testData.generateCsvHeaders();
        List<List<String>> data = testData.generateCsvData();

        writeDataToCsvFile(headers, data, getPathToCsvConfigFile());
    }

    /**
     * Writes the data to the CSV file specified by {@code pathToResultFileParam}.
     */
    private void writeDataToCsvFile(List<String> headers, List<List<String>> valuesList, String pathToResultFileParam)
            throws IOException {

        if (!createTestDataFolder()) {
            throw new IOException("Test data directory does not exist");
        }

        String pathToResultFile = (pathToResultFileParam.charAt(0) == '/' ? TestProperties.JMETER_TEST_DATA_DIRECTORY : "")
                + pathToResultFileParam;
        File file = new File(pathToResultFile);

        // Write data to the file; overwrite if it already exists
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            // Write header and data to the CSV file
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
     * Returns the data bundle stored in the file specified by {@code dataBundleJsonPath}.
     */
    protected DataBundle loadDataBundle(String dataBundleJsonPath) {
        try {
            String pathToJsonFile = (dataBundleJsonPath.charAt(0) == '/' ? TestProperties.JMETER_TEST_DATA_DIRECTORY : "")
                    + dataBundleJsonPath;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
     */
    protected void runJmeter(String jmxFile) throws Exception {
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        if (!TestProperties.JMETER_PROPERTIES_PATH.isEmpty()) {
            JMeterUtils.loadJMeterProperties(TestProperties.JMETER_PROPERTIES_PATH);
        }
        JMeterUtils.setJMeterHome(TestProperties.JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();

        // Load JMeter Test Plan
        File testFile = new File(TestProperties.JMETER_TEST_DIRECTORY + jmxFile);
        HashTree testPlanTree = SaveService.loadTree(testFile);

        // Create summariser for generating results file
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String resultFile = TestProperties.JMETER_TEST_RESULTS_DIRECTORY + jmxFile + ".jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // TODO: As mentioned in the docs, good to fail the test if the `success` value of any row is `false`.
        //  An example of when this occurs is if `email` is used for logging in instead of `googleid`.

        // TODO: Generate summary report from .jtl results file.
    }

}
