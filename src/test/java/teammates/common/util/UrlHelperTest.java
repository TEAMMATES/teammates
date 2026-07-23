package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static teammates.common.util.UrlHelper.encodeQueryParam;
import static teammates.common.util.UrlHelper.isSafeRelativeRedirectUrl;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UrlHelper}.
 */
public class UrlHelperTest extends BaseTestCase {

    @Test(dataProvider = "relativeUrls")
    public void testIsSafeRelativeRedirectUrl_relativeUrl_returnsTrue(String url) {
        assertTrue(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] relativeUrls() {
        return new Object[][] {
                {"/web/instructor/home"},
                {"/web/student/home?query=value"},
        };
    }

    @Test(dataProvider = "absoluteUrls")
    public void testIsSafeRelativeRedirectUrl_absoluteUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] absoluteUrls() {
        return new Object[][] {
                {"https://example.com/web/instructor/home"},
                {"http://example.com/web/student/home"},
        };
    }

    @Test(dataProvider = "externalUrls")
    public void testIsSafeRelativeRedirectUrl_externalUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] externalUrls() {
        return new Object[][] {
                {"https://example.com/web/instructor/home"},
                {"https://evil.example.com"},
                {"evil.com/web/instructor/home"},
                {"//evil.com/web/instructor/home"},
        };
    }

    @Test(dataProvider = "malformedUrls")
    public void testIsSafeRelativeRedirectUrl_malformedUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] malformedUrls() {
        return new Object[][] {
                {"https://[invalid"},
                {"example.com/invalid path"},
        };
    }

    @Test(dataProvider = "invalidRedirectUrls")
    public void testIsSafeRelativeRedirectUrl_invalidRedirectUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] invalidRedirectUrls() {
        return new Object[][] {
                {"?query=param"},
                {"web/instructor"},
                {"https://evil.com"},
                {"//evil.com"},
        };
    }

    @Test(dataProvider = "invalidUrls")
    public void testIsSafeRelativeRedirectUrl_invalidUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] invalidUrls() {
        return new Object[][] {
                {""},
                {null},
                {"javascript:alert(1)"},
        };
    }

    @Test(dataProvider = "unsupportedSchemaUrls")
    public void testIsSafeRelativeRedirectUrl_unsupportedSchemaUrl_returnsFalse(String url) {
        assertFalse(isSafeRelativeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] unsupportedSchemaUrls() {
        return new Object[][] {
                {"ftp://example.com/resource"},
                {"file:///path/to/file"},
                {"mailto:example@example.com"},
                {"../relative/path"},
        };
    }

    @Test
    public void testEncodeQueryParam_normalParam_returnsEncoded() {
        assertEquals("normal", encodeQueryParam("normal"));
    }

    @Test
    public void testEncodeQueryParam_paramWithSpaces_returnsEncoded() {
        assertEquals("with+spaces", encodeQueryParam("with spaces"));
    }

    @Test
    public void testEncodeQueryParam_paramWithSpecialChars_returnsEncoded() {
        assertEquals("with%2Fspecial%3Fchars%26", encodeQueryParam("with/special?chars&"));
    }

}
