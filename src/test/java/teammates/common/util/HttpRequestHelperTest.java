package teammates.common.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link HttpRequestHelper}.
 */
public class HttpRequestHelperTest extends BaseTestCase {

    private static final String TOKEN = "test-token";

    @Test
    public void testParseBearerTokenFromAuthorizationHeader() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader(null));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Basic " + TOKEN));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer"));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer "));
        assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer " + TOKEN));
        assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("bearer " + TOKEN));
        assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("BEARER " + TOKEN));
        assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer   " + TOKEN + "   "));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsEmptyAndTooShort() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader(""));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bear"));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsWrongScheme() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Digest"));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearerish " + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsMissingSeparatorAfterScheme() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer" + TOKEN));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer." + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsTabAfterScheme() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer\t" + TOKEN));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("bearer\t" + TOKEN));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer \t" + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsWhitespaceOnlyAfterScheme() {
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer "));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer  "));
        assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer \t "));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_acceptsB64tokenStyleCredential() {
        String b64style = "mF_9.B5f-4.1JqZ~token";
        assertEquals(b64style, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer " + b64style));
    }

}
