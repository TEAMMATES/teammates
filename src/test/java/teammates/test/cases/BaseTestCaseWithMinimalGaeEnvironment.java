package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BaseTestCaseWithMinimalGaeEnvironment extends BaseTestCaseWithObjectifyAccess {

    /**
     * Required to register a GAE API environment needed for creation of Datastore Key objects used in
     * defining parent-child relationships in entities.
     */
    private LocalServiceTestHelper helper = new LocalServiceTestHelper();

    @BeforeClass
    public void setUpGae() {
        helper.setUp();
    }

    @AfterClass
    public void tearDownGae() {
        helper.tearDown();
    }

}
