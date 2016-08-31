package teammates.client.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.util.Utils;
import teammates.test.util.FileHelper;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public final class DataBundleRegenerator {
    
    private DataBundleRegenerator() {
        // script-like, not meant to be instantiated
    }
    
    private static void regenerateDataBundleJson(File folder) throws Exception {
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().endsWith(".json") && !file.getName().startsWith("feedbackSessionTeamEvaluationTemplate")) {
                String jsonString = FileHelper.readFile(file.getCanonicalPath());
                Gson gson = Utils.getTeammatesGson();
                DataBundle db = gson.fromJson(jsonString, DataBundle.class);
                for (Map.Entry<String, FeedbackResponseAttributes> responseMap : db.feedbackResponses.entrySet()) {
                    fixResponse(responseMap.getValue());
                }
                for (Map.Entry<String, FeedbackQuestionAttributes> questionMap : db.feedbackQuestions.entrySet()) {
                    fixQuestion(questionMap.getValue());
                }
                String regeneratedJsonString = gson.toJson(db).replace("+0000", "UTC");
                FileHelper.saveFile(file.getCanonicalPath(), regeneratedJsonString);
                System.out.println(file.getCanonicalPath() + " regenerated!");
            }
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
        List<String> keys = new ArrayList<String>();
        for (Object key : json.keySet()) {
            keys.add((String) key);
        }
        Collections.sort(keys);
        for (String key : keys) {
            reprintedJson.put(key, json.get(key));
        }
        return reprintedJson;
    }
    
    private static void regenerateAllDataBundleJson() throws Exception {
        File folder = new File("./src/main/resources");
        regenerateDataBundleJson(folder);
        folder = new File("./src/test/resources/data");
        regenerateDataBundleJson(folder);
    }
    
    private static void regenerateSessionTemplateJson() throws Exception {
        File file = new File("./src/main/resources/feedbackSessionTeamEvaluationTemplate.json");
        regenerateGenericJson(file);
        String jsonString = FileHelper.readFile(file.getCanonicalPath());
        Gson gson = Utils.getTeammatesGson();
        List<FeedbackQuestionAttributes> template =
                gson.fromJson(jsonString, new TypeToken<List<FeedbackQuestionAttributes>>(){}.getType());
        for (FeedbackQuestionAttributes question : template) {
            fixQuestion(question);
        }
        String regeneratedJsonString = gson.toJson(template).replace("+0000", "UTC");
        FileHelper.saveFile(file.getCanonicalPath(), regeneratedJsonString);
        System.out.println(file.getCanonicalPath() + " regenerated!");
    }
    
    private static void regenerateGenericJson(File file) throws Exception {
        String jsonString = FileHelper.readFile(file.getCanonicalPath());
        JsonParser parser = new JsonParser();
        String regeneratedJsonString = Utils.getTeammatesGson().toJson(parser.parse(jsonString));
        FileHelper.saveFile(file.getCanonicalPath(), regeneratedJsonString);
        System.out.println(file.getCanonicalPath() + " regenerated!");
    }
    
    public static void main(String[] args) throws Exception {
        regenerateAllDataBundleJson();
        regenerateSessionTemplateJson();
    }
    
}
