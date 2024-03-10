package teammates.client.scripts.sql;

import java.io.IOException;

import teammates.client.connector.DatastoreClient;
import teammates.common.datatransfer.DataBundle;
import teammates.test.FileHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;

public class SeedTypicalDataBundle extends DatastoreClient {
    private final LogicExtension logic = new LogicExtension();

    protected String getSrcFolder() {
        return "src/client/java/teammates/client/scripts/sql/";
    }

    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getSrcFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    private void log(String logLine) {
        System.out.println(String.format("Seeding database: %s", logLine));
    }

    protected void persistData() {
        // Persisting basic data bundle
        DataBundle dataBundle = getTypicalDataBundle();
        try {
            logic.persistDataBundle(dataBundle);
        } catch (Exception e) {
            log(e.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        new SeedTypicalDataBundle().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        try {
            this.persistData();
        } catch (Exception e) {
            log(e.toString());
        }
    }
}
