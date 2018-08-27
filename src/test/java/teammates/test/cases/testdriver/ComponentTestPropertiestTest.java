package teammates.test.cases.testdriver;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.ComponentTestProperties;

/**
 * SUT: {@link ComponentTestProperties}.
 */
public class ComponentTestPropertiestTest extends BaseTestCase {

    @Test
    public void testGodModeFlag() {
        assertFalse(ComponentTestProperties.IS_GODMODE_ENABLED);
    }

}
