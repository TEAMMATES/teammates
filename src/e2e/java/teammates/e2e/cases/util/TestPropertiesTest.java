package teammates.e2e.cases.util;

import org.testng.annotations.Test;

import teammates.e2e.util.TestProperties;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testGodModeFlag() {
        assertFalse(TestProperties.IS_GODMODE_ENABLED);
    }

}
