package teammates.test.cases.util;

import org.testng.annotations.Test;

import teammates.common.util.RecaptchaVerifier;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link RecaptchaVerifier}.
 */
public class RecaptchaVerifierTest extends BaseTestCase {

    @Test
    public void testIsVerificationSuccessful() {
        ______TS("null or empty response");
        assertFalse(RecaptchaVerifier.isVerificationSuccessful(null));
        assertFalse(RecaptchaVerifier.isVerificationSuccessful(""));

        ______TS("any other key"); // true if using the official secret test key
        assertTrue(RecaptchaVerifier.isVerificationSuccessful("testResponseKey"));
        assertTrue(RecaptchaVerifier.isVerificationSuccessful(".#?"));
    }
}
