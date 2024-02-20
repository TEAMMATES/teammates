package teammates.client.scripts;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.test.FileHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;


public class SeedDb {
    private static final LocalDatastoreHelper LOCAL_DATASTORE_HELPER = LocalDatastoreHelper.newBuilder()
        .setConsistency(1.0)
        .setPort(8482)
        .setStoreOnDisk(false)
        .build();

    private final LogicExtension logic = new LogicExtension();
    private Closeable closeable;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    public void setupDbLayer() throws Exception {
        LOCAL_DATASTORE_HELPER.start();
        DatastoreOptions options = LOCAL_DATASTORE_HELPER.getOptions();
        ObjectifyService.init(new ObjectifyFactory(
                options.getService()
        ));
        OfyHelper.registerEntityClasses();

        LogicStarter.initializeDependencies();
    }

    public void setupObjectify() {
        closeable = ObjectifyService.begin();

    }

    public void tearDownObjectify() {
        closeable.close();
    }

    public void tearDownLocalDatastoreHelper() throws Exception {
        LOCAL_DATASTORE_HELPER.stop();
    }

    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    protected String getTestDataFolder() {
        return "src/test/resources/data/";
    }

    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getTestDataFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void removeAndRestoreTypicalDataBundle() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }

    protected void removeAndRestoreDataBundle(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        boolean isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        while (!isOperationSuccess && retryLimit > 0) {
            retryLimit--;
            System.out.println("Re-trying removeAndRestoreDataBundle");
            // Thread.sleep(OPERATION_RETRY_DELAY_IN_MS);
            isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        }
        assertTrue(isOperationSuccess);
    }

    protected boolean doRemoveAndRestoreDataBundle(DataBundle dataBundle) {
        try {
            // logic.removeDataBundle(dataBundle);
            logic.persistDataBundle(dataBundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean doPutDocuments(DataBundle dataBundle) {
        try {
            logic.putDocuments(dataBundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void verify() {
        for (StudentAttributes student : logic.getAllStudentsForEmail("student1InCourse1@gmail.tmt")) {
            System.out.println(String.format("Verify student: %s", student));
        }
        System.out.println(String.format("Verify account: %s", logic.getAccountsForEmail("instr1@course1.tmt")));
        System.out.println(String.format("Verify account: %s", logic.getAccountsForEmail("instr2@course1.tmt")));
    }

    public static void main(String[] args) throws Exception {
        SeedDb seedDb = new SeedDb();
        seedDb.setupDbLayer();
        seedDb.setupObjectify();

        seedDb.removeAndRestoreTypicalDataBundle();
        seedDb.verify();

        seedDb.tearDownObjectify();
        // seedDb.tearDownLocalDatastoreHelper();

    }
}
