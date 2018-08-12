package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.test.driver.GaeSimulation;

/**
 * Base class for all test cases which require a minimal GAE API environment registered.
 */
public class BaseTestCaseWithMinimalGaeEnvironment extends BaseTestCase {

    private LocalServiceTestHelper helper = new LocalServiceTestHelper();

    @BeforeClass
    public void setUpGae() {
        helper.setEnvAttributes(GaeSimulation.getEnvironmentAttributesWithApplicationHostname());
        helper.setUp();
    }

    @AfterClass
    public void tearDownGae() {
        helper.tearDown();
    }

}
