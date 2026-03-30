package teammates.logic.auth;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link GoogleOidcTokenVerifier}.
 */
public class GoogleOidcTokenVerifierTest extends BaseTestCase {

    /**
     * Malformed tokens must not be treated as logged-in users; the wrapper returns null on verification failure.
     * A full positive-path test would require a real signed Google ID token and is not run in unit tests.
     */
    @Test
    public void testVerify_malformedToken_returnsNull() {
        assertNull(GoogleOidcTokenVerifier.verify("not-a-valid-jwt"));
    }
}
