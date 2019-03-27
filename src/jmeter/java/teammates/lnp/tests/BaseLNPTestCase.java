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
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

    protected abstract String getPathToJsonDataFile();

    protected abstract String getPathToCsvConfigFile();

    /**
     * Returns the JSON object that is parsed from {@code pathToJsonInputFile}.
     */
    protected org.json.simple.JSONObject getJsonObjectFromFile() throws IOException, ParseException {
        String pathToJsonFile = (getPathToJsonDataFile().charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + getPathToJsonDataFile();

        JSONParser parser = new JSONParser();

        return (org.json.simple.JSONObject) parser.parse(Files.newBufferedReader(Paths.get(pathToJsonFile)));
    }

    /**
     * Creates the JSON test data and CSV config data files for the performance test.
     */
    protected void createTestData(LNPTestData testData) {
        try {
            writeJsonDataToFile(testData);
            createConfigDataCsvFile(testData);
        } catch (IOException | ParseException ex) {
            log.severe(TeammatesException.toStringWithStackTrace(ex));
        }
    }

    /**
     * Writes the JSON data to the file specified by {@code pathToOutputJson}.
     */
    private void writeJsonDataToFile(LNPTestData testData) throws IOException {
        if (!createTestDataFolder()) {
            throw new IOException("Test data directory does not exist");
        }

        JSONObject jsonData = testData.generateJsonData();
        String outputJsonPath = getPathToJsonDataFile();

        String pathToResultFile = (outputJsonPath.charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + outputJsonPath;
        File file = new File(pathToResultFile);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonData.toString());
        String prettyJsonString = gson.toJson(element);

        // Write data to the file (overwrite if it already exists)
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            bw.write(prettyJsonString);
            bw.flush();
        }
    }

    /**
     * Creates the CSV data and writes it to the file specified by {@code pathToCsvResultFile}.
     */
    private void createConfigDataCsvFile(LNPTestData testData) throws IOException, ParseException {
        List<String> headers = testData.getCsvHeaders();
        List<List<String>> data = testData.getCsvData();

        writeDataToCsvFile(headers, data, getPathToCsvConfigFile());
    }

    /**
     * Writes the data to the CSV file.
     */
    private void writeDataToCsvFile(List<String> headers, List<List<String>> valuesList, String pathToResultFileParam)
            throws IOException {

        if (!createTestDataFolder()) {
            throw new IOException("Test data directory does not exist");
        }

        String pathToResultFile = (pathToResultFileParam.charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + pathToResultFileParam;
        File file = new File(pathToResultFile);

        // Write data to the file (overwrite if it already exists)
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
            // Write header to the CSV file
            bw.write(convertToCsv(headers));

            // Write the data to the CSV file
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
     * Creates the test data folder if it does not exist.
     */
    private static boolean createTestDataFolder() {
        File testDataDirectory = new File(TestProperties.TEST_DATA_FOLDER);
        if (!testDataDirectory.exists()) {
            return testDataDirectory.mkdir();
        }
        return true;
    }

    /**
     * Adds the data bundle specified by {@code path} to the datastore.
     * @param path Path to the data bundle to be added
     */
    protected void persistTestData(String dataBundleJson) {
        DataBundle dataBundle = loadDataBundle(dataBundleJson);
        BackDoor.removeAndRestoreDataBundle(dataBundle);
    }

    /**
     * Deletes the data that was created from the file at {@code pathToJson} from the datastore.
     */
    protected void deleteTestData(String dataBundleJson) {
        DataBundle dataBundle = loadDataBundle(dataBundleJson);
        BackDoor.removeDataBundle(dataBundle);
    }

    protected DataBundle loadDataBundle(String dataBundleJson) {
        try {
            String pathToJsonFile = (dataBundleJson.charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                    + dataBundleJson;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void runJmeter(String jmxPath) throws Exception {
        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, logging, locale, etc.
        if (!TestProperties.JMETER_PROPERTIES_PATH.isEmpty()) {
            JMeterUtils.loadJMeterProperties(TestProperties.JMETER_PROPERTIES_PATH);
        }
        JMeterUtils.setJMeterHome(TestProperties.JMETER_HOME);
        JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        // CSV Config file path should be absolute, or relative to the project (eg. src/jmeter/resources/data/test.csv)
        File testFile = new File("src/jmeter/tests/" + jmxPath);
        HashTree testPlanTree = SaveService.loadTree(testFile);

        // Create summariser for generating results file
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String resultFile = "src/jmeter/results/" + jmxPath + ".jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

}
