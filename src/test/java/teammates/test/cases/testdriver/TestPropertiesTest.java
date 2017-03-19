package teammates.test.cases.testdriver;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TestProperties;

public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testConstruction() {
        assertTrue(null != TestProperties.TEAMMATES_VERSION);
    }
}
