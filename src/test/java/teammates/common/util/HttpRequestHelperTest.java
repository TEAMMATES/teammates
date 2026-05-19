package teammates.common.util;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link HttpRequestHelper}.
 */
public class HttpRequestHelperTest extends BaseTestCase {

    private static final String TOKEN = "test-token";

    @Test
    public void testParseBearerTokenFromAuthorizationHeader() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader(null));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Basic " + TOKEN));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer"));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer "));
        Assertions.assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer " + TOKEN));
        Assertions.assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("bearer " + TOKEN));
        Assertions.assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("BEARER " + TOKEN));
        Assertions.assertEquals(TOKEN, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer   " + TOKEN + "   "));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsEmptyAndTooShort() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader(""));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bear"));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsWrongScheme() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Digest"));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearerish " + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsMissingSeparatorAfterScheme() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer" + TOKEN));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer." + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsTabAfterScheme() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer\t" + TOKEN));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("bearer\t" + TOKEN));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer \t" + TOKEN));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_rejectsWhitespaceOnlyAfterScheme() {
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer "));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer  "));
        Assertions.assertNull(HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer \t "));
    }

    @Test
    public void testParseBearerTokenFromAuthorizationHeader_acceptsB64tokenStyleCredential() {
        String b64style = "mF_9.B5f-4.1JqZ~token";
        Assertions.assertEquals(b64style, HttpRequestHelper.parseBearerTokenFromAuthorizationHeader("Bearer " + b64style));
    }

}
