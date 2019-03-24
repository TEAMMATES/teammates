package teammates.lnp.scripts.create.data;

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

import teammates.lnp.util.TestProperties;

/**
 * Base class to create test data for performance tests.
 */
public abstract class CreateTestData {

    protected String pathToOutputJson;

    public String getPathToOutputJson() {
        return pathToOutputJson;
    }

    protected abstract JSONObject generateAccountsJson();

    protected abstract JSONObject generateCoursesJson();

    protected abstract JSONObject generateInstructorsJson();

    protected abstract JSONObject generateStudentsJson();

    protected abstract JSONObject generateFeedbackSessionsJson();

    protected abstract JSONObject generateFeedbackQuestionsJson();

    protected abstract JSONObject generateFeedbackResponsesJson();

    protected abstract JSONObject generateFeedbackResponseCommentsJson();

    protected abstract JSONObject generateProfilesJson();

    /**
     * Creates a JSON file with the relevant data for the performance test.
     */
    public JSONObject createJsonData() {
        JSONObject dataJson = new JSONObject();

        dataJson.put("accounts", generateAccountsJson());
        dataJson.put("courses", generateCoursesJson());
        dataJson.put("instructors", generateInstructorsJson());
        dataJson.put("students", generateStudentsJson());
        dataJson.put("feedbackSessions", generateFeedbackSessionsJson());
        dataJson.put("feedbackQuestions", generateFeedbackQuestionsJson());
        dataJson.put("feedbackResponses", generateFeedbackResponsesJson());
        dataJson.put("feedbackResponseComments", generateFeedbackResponseCommentsJson());
        dataJson.put("profiles", generateProfilesJson());

        return dataJson;
    }

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
