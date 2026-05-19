package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
