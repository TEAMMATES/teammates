package teammates.lnp.scripts.setup;

import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;
import teammates.lnp.scripts.LoadTestDataInDatastore;
import teammates.lnp.scripts.create.config.CreateStudentProfileTestConfigData;
import teammates.lnp.scripts.create.config.CreateTestConfigData;
import teammates.lnp.scripts.create.data.CreateStudentProfileTestData;
import teammates.lnp.scripts.create.data.CreateTestData;

/**
 *  Sets up the Student Profile performance test by generating the relevant data and creating entities in the datastore.
 */
public final class SetupStudentProfileTest {

    private static final Logger log = Logger.getLogger();

    private SetupStudentProfileTest() {
        // Intentional private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        CreateTestData dataCreator = new CreateStudentProfileTestData();
        CreateTestConfigData configDataCreator = new CreateStudentProfileTestConfigData();

        JSONObject jsonData = dataCreator.createJsonData();

        try {
            dataCreator.writeJsonDataToFile(jsonData);
            configDataCreator.createConfigDataCsvFile();
        } catch (IOException | ParseException ex) {
            log.severe(TeammatesException.toStringWithStackTrace(ex));
        }

        LoadTestDataInDatastore.addToDatastore(dataCreator.getPathToOutputJson());
    }

}
