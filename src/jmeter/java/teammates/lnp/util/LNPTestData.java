package teammates.lnp.util;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * L&P test data generator.
 */
public abstract class LNPTestData {

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
    public JSONObject generateJsonData() {
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
     * Returns list of header fields for the data in the CSV file to be generated.
     */
    public abstract List<String> generateCsvHeaders();

    /**
     * Returns the data for the CSV file to be generated.
     * The order of the field values should correspond to the order of headers
     * specified in {@link LNPTestData#generateCsvHeaders()}.
     *
     * @return List of entries, which are made up of a list of field values.
     */
    public abstract List<List<String>> generateCsvData() throws IOException, ParseException;

}
