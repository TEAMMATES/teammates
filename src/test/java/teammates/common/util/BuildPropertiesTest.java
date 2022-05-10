package teammates.common.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link Config}.
 */
public class BuildPropertiesTest extends BaseTestCase {

    @Test
    public void checkPresence() {
        String frontEndUrl = Config.getDefaultFrontEndUrl();
        assertNotNull(frontEndUrl);
        assertFalse(frontEndUrl.isBlank());
    }

}
