package teammates.test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.storage.api.OfyHelper;
import teammates.storage.search.InstructorSearchManager;
import teammates.storage.search.SearchManagerFactory;
import teammates.storage.search.StudentSearchManager;

/**
 * Base class for all test cases which require access to the Objectify service.
 */
public abstract class BaseTestCaseWithObjectifyAccess extends BaseTestCase {
    private static LocalDatastoreHelper localDatastoreHelper;
    private Closeable closeable;

    @BeforeSuite
    public void setupLocalDatastoreHelper() throws IOException, InterruptedException {
        localDatastoreHelper = LocalDatastoreHelper.newBuilder()
                .setConsistency(1.0)
                .setPort(TestProperties.TEST_LOCALDATASTORE_PORT)
                .setStoreOnDisk(false)
                .build();
        localDatastoreHelper.start();
    }

    @BeforeClass
    public void setupSearch() {
        SearchManagerFactory.registerInstructorSearchManager(
                new InstructorSearchManager(TestProperties.SEARCH_SERVICE_HOST, true));
        SearchManagerFactory.registerStudentSearchManager(
                new StudentSearchManager(TestProperties.SEARCH_SERVICE_HOST, true));
    }

    @BeforeClass
    public void setupObjectify() {
        DatastoreOptions options = localDatastoreHelper.getOptions();
        ObjectifyService.init(new ObjectifyFactory(
                options.getService()
        ));
        OfyHelper.registerEntityClasses();
        closeable = ObjectifyService.begin();
    }

    @AfterClass
    public void tearDownObjectify() {
        closeable.close();
    }

    @AfterClass
    public void resetLocalDatastoreHelper() throws IOException {
        localDatastoreHelper.reset();
    }

    @AfterClass
    public void resetSearchService() {
        SearchManagerFactory.getInstructorSearchManager().resetCollections();
        SearchManagerFactory.getStudentSearchManager().resetCollections();
    }

    @AfterSuite
    public void tearDownLocalDatastoreHelper() throws InterruptedException, TimeoutException, IOException {
        localDatastoreHelper.stop();
    }

}
