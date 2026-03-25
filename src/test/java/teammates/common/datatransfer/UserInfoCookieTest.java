package teammates.common.datatransfer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserInfoCookie}.
 */
public class UserInfoCookieTest extends BaseTestCase {
    private UserInfoCookie uc = new UserInfoCookie("MockId");

    @Test
    public void testIsValid() {
        ______TS("Cookie not expired");
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        assertTrue(uc.isValid());

        ______TS("Cookie expired");
        uc.setExpiryTime(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
        assertFalse(uc.isValid());

        ______TS("Cookie userId is empty");
        uc.setUserId("");
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        assertFalse(uc.isValid());

        ______TS("Cookie userId is null");
        uc.setUserId(null);
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        assertFalse(uc.isValid());
    }
}
