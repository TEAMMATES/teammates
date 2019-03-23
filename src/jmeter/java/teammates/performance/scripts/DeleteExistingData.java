package teammates.performance.scripts;

import java.io.IOException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.performance.util.BackDoor;
import teammates.performance.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * Script to delete existing data in the datastore.
 */
public class DeleteExistingData {

    public DeleteExistingData() {
        // Utility class
        // Intentional private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        deleteExistingData();
    }

    /**
     * Deletes the data from the datastore.
     */
    public static void deleteExistingData() {
        DataBundle dataBundle = loadDataBundle("/studentProfile.json");
        BackDoor.removeDataBundle(dataBundle);
    }

    private static DataBundle loadDataBundle(String pathToJsonFileParam) {
        try {
            String pathToJsonFile = (pathToJsonFileParam.charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                    + pathToJsonFileParam;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
