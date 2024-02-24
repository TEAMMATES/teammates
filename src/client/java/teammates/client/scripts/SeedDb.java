package teammates.client.scripts;

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

    public void setupDbLayer() throws Exception {
        LOCAL_DATASTORE_HELPER.start();
        DatastoreOptions options = LOCAL_DATASTORE_HELPER.getOptions();
        ObjectifyService.init(new ObjectifyFactory(
                options.getService()));
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

    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    protected void persistTypicalDataBundle() {
        DataBundle dataBundle = getTypicalDataBundle();
        try {
            logic.persistDataBundle(dataBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void verify() {
        for (StudentAttributes student : logic.getAllStudentsForEmail("student1InCourse1@gmail.tmt")) {
            System.out.println(String.format("Verify student: %s", student));
        }
        System.out.println(String.format("Verify account: %s", logic.getAccountsForEmail("instr1@course1.tmt")));
        System.out.println(String.format("Verify account: %s", logic.getAccountsForEmail("instr2@course1.tmt")));
    }

    protected void seedSetup() throws Exception {
        this.setupDbLayer();
        this.setupObjectify();

        this.persistTypicalDataBundle();
    }

    protected void seedTearDown() throws Exception {
        this.tearDownObjectify();
        this.tearDownLocalDatastoreHelper();
    }

    public static void main(String[] args) throws Exception {
        SeedDb seedDb = new SeedDb();
        seedDb.setupDbLayer();
        seedDb.setupObjectify();

        seedDb.persistTypicalDataBundle();
        seedDb.verify();

        seedDb.tearDownObjectify();
        seedDb.tearDownLocalDatastoreHelper();

    }
}
