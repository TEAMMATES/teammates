package teammates.client.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Text;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.JsonUtils;
import teammates.test.driver.FileHelper;

public final class DataBundleRegenerator {

    private static final List<String> NON_DATA_BUNDLE_JSON = Arrays.asList(
            "feedbackSessionTeamEvaluationTemplate.json"
    );

    private DataBundleRegenerator() {
        // script-like, not meant to be instantiated
    }

    private static void regenerateDataBundleJson(File folder) throws IOException {
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return;
        }
        for (File file : listOfFiles) {
            if (!file.getName().endsWith(".json") || NON_DATA_BUNDLE_JSON.contains(file.getName())) {
                continue;
            }
            String jsonString = FileHelper.readFile(file.getCanonicalPath());
            DataBundle db = JsonUtils.fromJson(jsonString, DataBundle.class);
            db.feedbackResponses.forEach((key, feedbackResponseAttributes) -> fixResponse(feedbackResponseAttributes));
            db.feedbackQuestions.forEach((key, feedbackQuestionAttributes) -> fixQuestion(feedbackQuestionAttributes));
            String regeneratedJsonString = JsonUtils.toJson(db).replace("+0000", "UTC");
            saveFile(file.getCanonicalPath(), regeneratedJsonString);
        }
    }

    private static void fixResponse(FeedbackResponseAttributes response) {
        String responseValue = response.responseMetaData.getValue();
        try {
            JSONObject responseJson = maintainKeyOrder(new JSONObject(responseValue));
            response.responseMetaData = new Text(responseJson.toString());
        } catch (JSONException e) {
            response.responseMetaData = new Text(responseValue);
        }
    }

    private static void fixQuestion(FeedbackQuestionAttributes question) {
        String questionValue = question.questionMetaData.getValue();
        try {
            JSONObject questionJson = maintainKeyOrder(new JSONObject(questionValue));
            question.questionMetaData = new Text(questionJson.toString());
        } catch (JSONException e) {
            question.questionMetaData = new Text(questionValue);
        }
    }

    private static JSONObject maintainKeyOrder(JSONObject json) {
        JSONObject reprintedJson = new JSONObject();
        List<String> keys = new ArrayList<>();
        for (Object key : json.keySet()) {
            keys.add((String) key);
        }
        keys.sort(null);
        for (String key : keys) {
            reprintedJson.put(key, json.get(key));
        }
        return reprintedJson;
    }

    private static void regenerateAllDataBundleJson() throws IOException {
        File folder = new File("./src/main/resources");
        regenerateDataBundleJson(folder);
        folder = new File("./src/test/resources/data");
        regenerateDataBundleJson(folder);
    }

    private static void regenerateSessionTemplateJson() throws IOException {
        File file = new File("./src/main/resources/feedbackSessionTeamEvaluationTemplate.json");
        regenerateGenericJson(file);
        String jsonString = FileHelper.readFile(file.getCanonicalPath());
        List<FeedbackQuestionAttributes> template =
                JsonUtils.fromJson(jsonString, new TypeToken<List<FeedbackQuestionAttributes>>(){}.getType());
        for (FeedbackQuestionAttributes question : template) {
            fixQuestion(question);
        }
        String regeneratedJsonString = JsonUtils.toJson(template).replace("+0000", "UTC");
        saveFile(file.getCanonicalPath(), regeneratedJsonString);
    }

    private static void regenerateGenericJson(File file) throws IOException {
        String jsonString = FileHelper.readFile(file.getCanonicalPath());
        String regeneratedJsonString = JsonUtils.toJson(JsonUtils.parse(jsonString));
        saveFile(file.getCanonicalPath(), regeneratedJsonString);
    }

    private static void saveFile(String filePath, String content) throws IOException {
        FileHelper.saveFile(filePath, content);
        System.out.println(filePath + " regenerated!");
    }

    private static void regenerateMapsJson() throws IOException {
        File file = new File("./src/main/webapp/js/countryCoordinates.json");
        regenerateGenericJson(file);
        file = new File("./src/main/webapp/js/userMapData.json");
        regenerateGenericJson(file);
    }

    public static void main(String[] args) throws IOException {
        regenerateAllDataBundleJson();
        regenerateSessionTemplateJson();
        regenerateMapsJson();
    }

}
