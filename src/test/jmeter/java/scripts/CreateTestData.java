package scripts;

import java.io.IOException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.e2e.util.BackDoor;
import teammates.test.driver.FileHelper;

/**
 * Script to create test data for performance test.
 */
public class CreateTestData {

    private static final String JMETER_DATA_FOLDER = "src/test/jmeter/resources/data";

    public static void main(String[] args) {
        DataBundle dataBundle =
                loadDataBundle("/testData.json");
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
