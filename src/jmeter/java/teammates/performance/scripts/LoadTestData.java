package teammates.performance.scripts;

import java.io.IOException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.performance.util.BackDoor;
import teammates.performance.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * Script for performance tests to manage the data in the local datastore.
 */
public final class LoadTestData {

    private LoadTestData() {
        // Utility class
        // Intentional private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        addToDatastore("/studentProfile.json");
    }

    /**
     * Adds the data bundle specified by {@code path} to the datastore.
     * @param path Path to the data bundle to be added
     */
    public static void addToDatastore(String path) {
        DataBundle dataBundle = loadDataBundle(path);
        BackDoor.removeAndRestoreDataBundle(dataBundle);
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
