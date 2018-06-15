package teammates.test.cases;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.test.driver.TestProperties;

/**
 * Base class for all test cases which require a minimal GAE API environment registered.
 */
public class BaseTestCaseWithMinimalGaeEnvironment extends BaseTestCase {

    private LocalServiceTestHelper helper = new LocalServiceTestHelper();

    @BeforeClass
    public void setUpGae() {
        helper.setEnvAttributes(generateEnvironmentAttributesWithApplicationHostnameSet());
        helper.setUp();
    }

    @AfterClass
    public void tearDownGae() {
        helper.tearDown();
    }

    public Map<String, Object> generateEnvironmentAttributesWithApplicationHostnameSet() {
        Map<String, Object> attributes = new HashMap<>();
        try {
            attributes.put("com.google.appengine.runtime.default_version_hostname",
                    new URL(TestProperties.TEAMMATES_URL).getAuthority());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return attributes;
    }

}
