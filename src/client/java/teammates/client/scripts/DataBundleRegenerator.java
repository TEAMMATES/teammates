package teammates.client.scripts;

import java.io.File;

import teammates.common.util.Utils;
import teammates.test.util.FileHelper;

import com.google.gson.JsonParser;

public final class DataBundleRegenerator {
    
    private DataBundleRegenerator() {
        // script-like, not meant to be instantiated
    }
    
    private static void regenerateDataBundleJson(File folder) throws Exception {
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().endsWith(".json") && !file.getName().startsWith("feedbackSessionTeamEvaluationTemplate")) {
                regenerateGenericJson(file);
            }
        }
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
