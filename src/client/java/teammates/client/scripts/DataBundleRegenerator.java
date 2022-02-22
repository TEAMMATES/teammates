package teammates.client.scripts;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.JsonUtils;
import teammates.test.FileHelper;

/**
 * Regenerates all JSON data files used for different purposes.
 */
public final class DataBundleRegenerator {

    private static final List<String> NON_DATA_BUNDLE_JSON = Arrays.asList(
            "feedbackSessionTeamEvaluationTemplate.json",
            "package.json"
    );

    private DataBundleRegenerator() {
        // script-like, not meant to be instantiated
    }

    private static void regenerateDataBundleJson(File folder) throws IOException {
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            listOfFiles = new File[] {};
        }
        for (File file : listOfFiles) {
            if (!file.getName().endsWith(".json") || NON_DATA_BUNDLE_JSON.contains(file.getName())) {
                continue;
            }
            String jsonString = FileHelper.readFile(file.getCanonicalPath());
            DataBundle db = JsonUtils.fromJson(jsonString, DataBundle.class);
            String regeneratedJsonString = JsonUtils.toJson(db).replace("+0000", "UTC");
            saveFile(file.getCanonicalPath(), regeneratedJsonString + System.lineSeparator());
        }
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
        String regeneratedJsonString = JsonUtils.toJson(template).replace("+0000", "UTC");
        saveFile(file.getCanonicalPath(), regeneratedJsonString + System.lineSeparator());
    }

    private static void regenerateGenericJson(File file) throws IOException {
        String jsonString = FileHelper.readFile(file.getCanonicalPath());
        String regeneratedJsonString = JsonUtils.toJson(JsonUtils.parse(jsonString));
        saveFile(file.getCanonicalPath(), regeneratedJsonString + System.lineSeparator());
    }

    private static void saveFile(String filePath, String content) throws IOException {
        FileHelper.saveFile(filePath, content);
        System.out.println(filePath + " regenerated!");
    }

    private static void regenerateWebsiteDataJson() throws IOException {
        File[] listOfFiles = new File("./src/main/webapp/data").listFiles();
        if (listOfFiles == null) {
            listOfFiles = new File[] {};
        }
        for (File file : listOfFiles) {
            if (!file.getName().endsWith(".json")) {
                continue;
            }
            regenerateGenericJson(file);
        }
    }

    public static void main(String[] args) throws IOException {
        regenerateAllDataBundleJson();
        regenerateSessionTemplateJson();
        regenerateWebsiteDataJson();
    }

}
