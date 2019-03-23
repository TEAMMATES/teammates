package teammates.performance.scripts;

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
     * Creates a JSON file with the necessary test data.
     */
    public abstract JSONObject createJsonData();

    /**
     * Writes the JSON data to the file.
     */
    protected static void writeJsonDataToFile(JSONObject data, String pathToResultFileParam) {

        String pathToResultFile = (pathToResultFileParam.charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                + pathToResultFileParam;
        File file = new File(pathToResultFile);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(data.toString());
        String prettyJsonString = gson.toJson(element);

        // if file doesnt exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(pathToResultFile))) {
                bw.write(prettyJsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
