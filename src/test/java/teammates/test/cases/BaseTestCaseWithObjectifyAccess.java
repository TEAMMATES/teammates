package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.storage.api.OfyHelper;

/**
 * Test cases that need access to the Objectify service.
 */
public abstract class BaseTestCaseWithObjectifyAccess extends BaseTestCase {
    private Closeable closeable;

    @BeforeClass
    public void setupObjectify() {
        OfyHelper.registerEntityClasses();
        closeable = ObjectifyService.begin();
    }

    @AfterClass
    public void tearDownObjectify() {
        closeable.close();
    }

}
