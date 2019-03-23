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
public class DeleteTestData {

    protected String pathToJson;

    public String getPathToJson() {
        return pathToJson;
    }

    /**
     * Deletes the data that was created from the file at {@code pathToJson} from the datastore.
     */
    public void deleteTestData() {
        DataBundle dataBundle = loadDataBundle();
        BackDoor.removeDataBundle(dataBundle);
    }

    protected DataBundle loadDataBundle() {
        try {
            String pathToJsonFile = (getPathToJson().charAt(0) == '/' ? TestProperties.TEST_DATA_FOLDER : "")
                    + getPathToJson();
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
