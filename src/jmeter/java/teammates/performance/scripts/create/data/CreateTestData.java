package teammates.performance.scripts.create.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import teammates.performance.util.TestProperties;

/**
 * Base class to create test data for performance tests.
 */
public abstract class CreateTestData {

    protected String pathToOutputJson;

    public String getPathToOutputJson() {
        return pathToOutputJson;
    }

    /**
     * Creates a JSON file with the relevant data for the performance test.
     */
    public abstract JSONObject createJsonData();

    /**
     * Writes the JSON data to the file specified by {@code pathToOutputJson}.
     */
    public void writeJsonDataToFile(JSONObject data) throws IOException {

        if (!TestProperties.createTestDataFolder()) {
            throw new IOException("Test data directory does not exist");
        }

        String pathToResultFile = (getPathToOutputJson().charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + getPathToOutputJson();
        File file = new File(pathToResultFile);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(data.toString());
        String prettyJsonString = gson.toJson(element);

        // Write data to the file (overwrite if it already exists)
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile));
        bw.write(prettyJsonString);
        bw.flush();
        bw.close();
    }
}
