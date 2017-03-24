package teammates.test.cases.util;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link Config}.
 */
public class BuildPropertiesTest extends BaseTestCase {

    @Test
    public void checkPresence() {
        assertNotNull(Config.APP_URL);
        assertTrue(Config.PERSISTENCE_CHECK_DURATION >= 0);
    }

}
