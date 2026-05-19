package teammates.e2e.util;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testContent() {
        Assertions.assertNotNull(TestProperties.TEAMMATES_FRONTEND_URL);
        Assertions.assertNotNull(TestProperties.TEAMMATES_BACKEND_URL);
    }

}
