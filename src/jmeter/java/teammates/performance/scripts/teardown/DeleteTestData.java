package teammates.performance.scripts.teardown;

import java.io.IOException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.performance.util.BackDoor;
import teammates.performance.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * Base class to delete the performance test data that is present in the datastore.
 */
public abstract class DeleteTestData {

    protected String pathToJson;

    public String getPathToJson() {
        return pathToJson;
    }

    /**
     * Deletes the data from the datastore.
     */
    public void deleteTestData(String pathToJsonFileParam) {
        DataBundle dataBundle = loadDataBundle(pathToJsonFileParam);
        BackDoor.removeDataBundle(dataBundle);
    }

    protected DataBundle loadDataBundle(String pathToJsonFileParam) {
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
