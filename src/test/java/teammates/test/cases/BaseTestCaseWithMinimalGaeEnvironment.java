package teammates.test.cases;

import java.net.MalformedURLException;
import java.net.URL;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.test.driver.GaeSimulation;
import teammates.test.driver.TestProperties;

/**
 * Base class for all test cases which require a minimal GAE API environment registered.
 */
public class BaseTestCaseWithMinimalGaeEnvironment extends BaseTestCase {

    private LocalServiceTestHelper helper = new LocalServiceTestHelper();

    /**
     * Sets the environment of SystemProperty based on the URL.
     */
    @BeforeSuite
    public void setUpEnvironment() throws MalformedURLException {
        if (new URL(TestProperties.TEAMMATES_URL).getHost().contains(".appspot.com")) {
            SystemProperty.environment.set(SystemProperty.Environment.Value.Production);
        } else {
            SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
        }
    }

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
