package teammates.common.datatransfer;

import org.junit.jupiter.api.Assertions;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserInfoCookie}.
 */
public class UserInfoCookieTest extends BaseTestCase {
    private UserInfoCookie uc = new UserInfoCookie("MockId", UUID.randomUUID());

    @Test
    public void testIsValid() {
        ______TS("Cookie not expired");
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        Assertions.assertTrue(uc.isValid());

        ______TS("Cookie expired");
        uc.setExpiryTime(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
        Assertions.assertFalse(uc.isValid());

        ______TS("Cookie with null user ID");
        uc.setUserId(null);
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        Assertions.assertFalse(uc.isValid());

        ______TS("Cookie with empty user ID");
        uc.setUserId("");
        Assertions.assertFalse(uc.isValid());

        ______TS("Cookie with blank user ID");
        uc.setUserId("   ");
        Assertions.assertFalse(uc.isValid());
    }

}
