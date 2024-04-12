package teammates.lnp.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.lnp.util.BackDoor;
import teammates.lnp.util.LNPResultsStatistics;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPSqlTestData;
import teammates.lnp.util.TestProperties;
import teammates.test.BaseTestCase;
import teammates.test.FileHelper;

/**
 * Base class for all L&P test cases.
 */
public abstract class BaseLNPTestCase extends BaseTestCase {

    static final String GET = HttpGet.METHOD_NAME;
    static final String POST = HttpPost.METHOD_NAME;
    static final String PUT = HttpPut.METHOD_NAME;
    static final String DELETE = HttpDelete.METHOD_NAME;

    private static final Logger log = Logger.getLogger();

    private static final int RESULT_COUNT = 3;

    final BackDoor backdoor = BackDoor.getInstance();
    String timeStamp;
    LNPSpecification specification;

    /**
     * Returns the test data used for the current test.
     */
    protected abstract LNPSqlTestData getTestData();

    /**
     * Returns the JMeter test plan for the L&P test case.
     * @return A nested tree structure that consists of the various elements that are used in the JMeter test.
     */
    protected abstract ListedHashTree getLnpTestPlan();

    /**
     * Sets up the specification for this L&P test case.
     */
    protected abstract void setupSpecification();

    /**
     * Returns the path to the generated JSON data bundle file.
     */
    protected String getJsonDataPath() {
        return "/" + getClass().getSimpleName() + timeStamp + ".json";
    }

    /**
     * Returns the path to the generated JMeter CSV config file.
     */
    protected String getCsvConfigPath() {
        return "/" + getClass().getSimpleName() + "Config" + timeStamp + ".csv";
    }

    /**
     * Returns the path to the generated JTL test results file.
     */
    protected String getJtlResultsPath() {
        return "/" + getClass().getSimpleName() + timeStamp + ".jtl";
    }

    @Override
    protected String getTestDataFolder() {
        return TestProperties.LNP_TEST_DATA_FOLDER;
    }

    /**
     * Returns the path to the data file, relative to the project root directory.
     */
    protected String getPathToTestDataFile(String fileName) {
        return getTestDataFolder() + fileName;
    }

    /**
     * Returns the path to the JSON test results statistics file, relative to the project root directory.
     */
    private String getPathToTestStatisticsResultsFile() {
        return String.format("%s/%sStatistics%s.json", TestProperties.LNP_TEST_RESULTS_FOLDER,
                        this.getClass().getSimpleName(), this.timeStamp);
    }

    String createFileAndDirectory(String directory, String fileName) throws IOException {
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
    void createJsonDataFile(LNPSqlTestData testData) throws IOException {
        SqlDataBundle jsonData = testData.generateJsonData();

        String pathToResultFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getJsonDataPath());
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            bw.write(JsonUtils.toJson(jsonData, SqlDataBundle.class));
            bw.flush();
        }
    }

    /**
     * Creates the CSV data and writes it to the file specified by {@link #getCsvConfigPath()}.
     */
    private void createCsvConfigDataFile(LNPSqlTestData testData) throws IOException {
        List<String> headers = testData.generateCsvHeaders();
        List<List<String>> valuesList = testData.generateCsvData();

        String pathToCsvFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getCsvConfigPath());
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
    String convertToCsv(List<String> values) {
        StringJoiner csvRow = new StringJoiner("|", "", "\n");
        for (String value : values) {
            csvRow.add(value);
        }
        return csvRow.toString();
    }

    /**
     * Returns the L&P test results statistics.
     * @return The initialized result statistics from the L&P test results.
     * @throws IOException if there is an error when loading the result file.
     */
    private LNPResultsStatistics getResultsStatistics() throws IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get(getPathToTestStatisticsResultsFile())));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        JsonObject endpointStats = jsonObject.getAsJsonObject("HTTP Request Sampler");
        return gson.fromJson(endpointStats, LNPResultsStatistics.class);
    }

    /**
     * Renames the default results statistics file to the name of the test.
     */
    private void renameStatisticsFile() {
        File defaultFile = new File(TestProperties.LNP_TEST_RESULTS_FOLDER + "/statistics.json");
        File lnpStatisticsFile = new File(getPathToTestStatisticsResultsFile());

        if (lnpStatisticsFile.exists()) {
            lnpStatisticsFile.delete();
        }
        if (!defaultFile.renameTo(lnpStatisticsFile)) {
            log.warning("Failed to rename generated statistics.json file.");
        }
    }

    /**
     * Setup and load the JMeter configuration and property files to run the Jmeter test.
     * @throws IOException if the save service properties file cannot be loaded.
     */
    private void setJmeterProperties() throws IOException {
        JMeterUtils.loadJMeterProperties(TestProperties.JMETER_PROPERTIES_PATH);
        JMeterUtils.setJMeterHome(TestProperties.JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();
    }

    /**
     * Creates the JSON test data and CSV config data files for the performance test from {@code testData}.
     */
    protected void createTestData() throws IOException, HttpRequestFailedException {
        LNPSqlTestData testData = getTestData();
        createJsonDataFile(testData);
        persistTestData();
        createCsvConfigDataFile(testData);
    }

    /**
     * Creates the entities in the database from the JSON data file.
     */
    protected void persistTestData() throws IOException, HttpRequestFailedException {
        SqlDataBundle dataBundle = loadSqlDataBundle(getJsonDataPath());
        SqlDataBundle responseBody = backdoor.removeAndRestoreSqlDataBundle(dataBundle);

        String pathToResultFile = createFileAndDirectory(TestProperties.LNP_TEST_DATA_FOLDER, getJsonDataPath());
        String jsonValue = JsonUtils.toJson(responseBody, SqlDataBundle.class);
        FileHelper.saveFile(pathToResultFile, jsonValue);
    }

    /**
     * Display the L&P results on the console.
     */
    protected void displayLnpResults() throws IOException {
        LNPResultsStatistics resultsStats = getResultsStatistics();

        resultsStats.displayLnpResultsStatistics();
        specification.verifyLnpTestSuccess(resultsStats);
    }

    /**
     * Runs the JMeter test.
     * @param shouldCreateJmxFile true if the generated test plan should be saved to a `.jmx` file which
     *                            can be opened in the JMeter GUI, and false otherwise.
     */
    protected void runJmeter(boolean shouldCreateJmxFile) throws IOException {
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        setJmeterProperties();

        HashTree testPlan = getLnpTestPlan();

        if (shouldCreateJmxFile) {
            String pathToConfigFile = createFileAndDirectory(
                    TestProperties.LNP_TEST_CONFIG_FOLDER, "/" + getClass().getSimpleName() + ".jmx");
            SaveService.saveTree(testPlan, Files.newOutputStream(Paths.get(pathToConfigFile)));
        }

        // Add result collector to the test plan for generating results file
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }

        String resultsFile = createFileAndDirectory(TestProperties.LNP_TEST_RESULTS_FOLDER, getJtlResultsPath());
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(resultsFile);
        testPlan.add(testPlan.getArray()[0], resultCollector);

        // Run Jmeter Test
        jmeter.configure(testPlan);
        jmeter.run();

        try {
            ReportGenerator reportGenerator = new ReportGenerator(resultsFile, null);
            reportGenerator.generate();
        } catch (ConfigurationException | GenerationException e) {
            log.warning(e.getMessage());
        }

        renameStatisticsFile();
    }

    /**
     * Deletes the data that was created in the database from the JSON data file.
     */
    protected void deleteTestData() {
        SqlDataBundle dataBundle = loadSqlDataBundle(getJsonDataPath());
        backdoor.removeSqlDataBundle(dataBundle);
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

    /**
     * Deletes the oldest excess result .jtl file and the statistics file, if there are more than RESULT_COUNT.
     */
    protected void cleanupResults() throws IOException {
        File[] fileList = new File(TestProperties.LNP_TEST_RESULTS_FOLDER)
                .listFiles((d, s) -> {
                    return s.contains(this.getClass().getSimpleName());
                });
        if (fileList == null) {
            fileList = new File[] {};
        }
        Arrays.sort(fileList, (a, b) -> {
            return b.getName().compareTo(a.getName());
        });

        int jtlCounter = 0;
        int statisticsCounter = 0;
        for (File file : fileList) {
            if (file.getName().contains("Statistics")) {
                statisticsCounter++;
                if (statisticsCounter > RESULT_COUNT) {
                    Files.delete(file.toPath());
                }
            } else {
                jtlCounter++;
                if (jtlCounter > RESULT_COUNT) {
                    Files.delete(file.toPath());
                }
            }
        }
    }

    /**
     * Sanitize the string to be CSV-safe string.
     */
    protected String sanitizeForCsv(String originalString) {
        return String.format("\"%s\"", originalString.replace(System.lineSeparator(), "").replace("\"", "\"\""));
    }

    /**
     * Generates timestamp for generated statistics/CSV files in order to prevent concurrency issues.
     */
    protected void generateTimeStamp() {
        this.timeStamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("_uuuuMMddHHmmss"));
    }
}
