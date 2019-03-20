package teammates.performance.scripts;

import java.io.IOException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.e2e.util.BackDoor;
import teammates.test.driver.FileHelper;

/**
 * Script for performance tests to manage the data in the local datastore.
 */
public class LoadTestData {

    private static final String JMETER_DATA_FOLDER = "src/test/jmeter/resources/data";

    public static void main(String[] args) {
        add("/testData.json");
    }

    public static void add(String path) {
        DataBundle dataBundle = loadDataBundle(path);
        BackDoor.removeAndRestoreDataBundle(dataBundle);
    }

    private static DataBundle loadDataBundle(String pathToJsonFileParam) {
        try {
            String pathToJsonFile = (pathToJsonFileParam.charAt(0) == '/' ? JMETER_DATA_FOLDER : "")
                    + pathToJsonFileParam;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
