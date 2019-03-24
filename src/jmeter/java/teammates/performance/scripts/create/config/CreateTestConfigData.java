package teammates.performance.scripts.create.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import teammates.performance.util.TestProperties;

/**
 *  Base class to create the CSV config data for JMeter performance tests from the corresponding JSON data.
 */
public abstract class CreateTestConfigData {

    protected String pathToCsvResultFile;
    protected String pathToJsonInputFile;

    public String getPathToCsvResultFile() {
        return pathToCsvResultFile;
    }

    public String getPathToJsonInputFile() {
        return pathToJsonInputFile;
    }

    /**
     * Returns list of header fields for the data in the CSV file to be generated.
     */
    protected abstract List<String> getCsvHeaders();

    /**
     * Returns the data for the CSV file to be generated. The order of the field values should correspond to
     * the order of headers specified in {@link CreateTestConfigData#getCsvData()}.
     *
     * @return List of entries, which are made up of a list of field values.
     */
    protected abstract List<List<String>> getCsvData() throws IOException, ParseException;

    /**
     * Creates the CSV data and writes it to the file specified by {@code pathToCsvResultFile}.
     */
    public void createConfigDataCsvFile() throws IOException, ParseException {
        List<String> headers = getCsvHeaders();
        List<List<String>> data = getCsvData();

        writeDataToCsvFile(headers, data, getPathToCsvResultFile());
    }

    /**
     * Returns the JSON object that is parsed from {@code pathToJsonInputFile}.
     */
    protected JSONObject getJsonObjectFromFile() throws IOException, ParseException {
        String pathToJsonFile = (getPathToJsonInputFile().charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + getPathToJsonInputFile();

        JSONParser parser = new JSONParser();

        return (JSONObject) parser.parse(Files.newBufferedReader(Paths.get(pathToJsonFile)));
    }

    /**
     * Writes the data to the CSV file.
     */
    protected void writeDataToCsvFile(List<String> headers, List<List<String>> valuesList, String pathToResultFileParam)
            throws IOException {

        if (!TestProperties.createTestDataFolder()) {
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

        BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile));

        // Write header to the CSV file
        bw.write(convertToCsv(headers));

        // Write the data to the CSV file
        for (List<String> values : valuesList) {
            bw.write(convertToCsv(values));
        }

        bw.flush();
        bw.close();
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
}
