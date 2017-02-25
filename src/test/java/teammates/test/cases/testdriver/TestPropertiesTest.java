package teammates.test.cases.testdriver;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TestProperties;

public class TestPropertiesTest extends BaseTestCase {
    @Test
    public void testExtractVersionNumber() {
        assertEquals("4.18", TestProperties.extractVersionNumber("abc<version>4-18</version>xyz"));
    }

    @Test
    public void testConstruction() {
        assertTrue(null != TestProperties.TEAMMATES_VERSION);
    }
}
