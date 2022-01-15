package teammates.e2e.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testContent() {
        assertNotNull(TestProperties.TEAMMATES_FRONTEND_URL);
        assertNotNull(TestProperties.TEAMMATES_BACKEND_URL);
    }

}
