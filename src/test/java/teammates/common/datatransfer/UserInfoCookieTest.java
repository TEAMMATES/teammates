package teammates.common.datatransfer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserInfoCookie}.
 */
public class UserInfoCookieTest extends BaseTestCase {
    private UserInfoCookie uc = new UserInfoCookie(UUID.randomUUID());

    @Test
    public void testIsValid() {
        ______TS("Cookie not expired");
        uc.setExpiryTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        assertTrue(uc.isValid());

        ______TS("Cookie expired");
        uc.setExpiryTime(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
        assertFalse(uc.isValid());
    }

}
