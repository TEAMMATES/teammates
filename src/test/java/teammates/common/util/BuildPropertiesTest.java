package teammates.common.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCaseWithMinimalGaeEnvironment;

/**
 * SUT: {@link Config}.
 */
public class BuildPropertiesTest extends BaseTestCaseWithMinimalGaeEnvironment {

    @Test
    public void checkPresence() {
        assertNotNull(Config.getBaseAppUrl());
    }

}
