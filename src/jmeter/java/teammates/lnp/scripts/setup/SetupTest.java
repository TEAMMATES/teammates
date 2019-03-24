package teammates.lnp.scripts.setup;

import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.lnp.scripts.LoadTestDataInDatastore;
import teammates.lnp.scripts.create.config.CreateTestConfigData;
import teammates.lnp.scripts.create.data.CreateTestData;

/**
 *  Sets up the Student Profile performance test by generating the relevant data and creating entities in the datastore.
 */
public class SetupTest {

    private static final Logger log = Logger.getLogger();

    /**
     * Creates the JSON test data and CSV config data files for the performance test.
     */
    protected String createTestDataAndConfig(CreateTestData dataCreator, CreateTestConfigData configDataCreator) {
        JSONObject jsonData = dataCreator.createJsonData();

        try {
            dataCreator.writeJsonDataToFile(jsonData);
            configDataCreator.createConfigDataCsvFile();
        } catch (IOException | ParseException ex) {
            log.severe(TeammatesException.toStringWithStackTrace(ex));
        } finally {
            return dataCreator.getPathToOutputJson();
        }
    }

    /**
     * Sets up the performance test by creating necessary data and adding it to the datastore.
     */
    protected void setupTestData(CreateTestData dataCreator, CreateTestConfigData configDataCreator) {
        String pathToResultJson = createTestDataAndConfig(dataCreator, configDataCreator);
        LoadTestDataInDatastore.addToDatastore(pathToResultJson);
    }
}
