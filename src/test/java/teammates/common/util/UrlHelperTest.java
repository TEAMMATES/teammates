package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static teammates.common.util.UrlHelper.encodeQueryParam;
import static teammates.common.util.UrlHelper.getRelativeUrl;
import static teammates.common.util.UrlHelper.isSafeRedirectUrl;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UrlHelper}.
 */
public class UrlHelperTest extends BaseTestCase {

    @Test(dataProvider = "relativeUrls")
    public void testIsSafeRedirectUrl_relativeUrl_returnsTrue(String url) {
        assertTrue(isSafeRedirectUrl(url));
    }

    @Test(dataProvider = "configuredFrontendUrls")
    public void testIsSafeRedirectUrl_configuredFrontendUrl_returnsTrue(String url) {
        assertTrue(isSafeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] configuredFrontendUrls() {
        return new Object[][] {
                {Config.APP_FRONTEND_URL + "/web/instructor/home"},
                {Config.APP_FRONTEND_URL + "/web/student/home?query=value"},
        };
    }

    @Test(dataProvider = "externalUrls")
    public void testIsSafeRedirectUrl_externalUrl_returnsFalse(String url) {
        assertFalse(isSafeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] externalUrls() {
        return new Object[][] {
                {"https://example.com/web/instructor/home"},
                {"https://evil.example.com"},
        };
    }

    @Test
    public void testIsSafeRedirectUrl_differentPort_returnsFalse() throws URISyntaxException {
        URI frontendUri = new URI(Config.APP_FRONTEND_URL);
        int differentPort = frontendUri.getPort() == 8080 ? 8081 : 8080;
        String url = String.format("%s://%s:%d/web/instructor/home",
                frontendUri.getScheme(), frontendUri.getHost(), differentPort);

        assertFalse(isSafeRedirectUrl(url));
    }

    @Test
    public void testIsSafeRedirectUrl_protocolRelativeUrl_returnsFalse() {
        assertFalse(isSafeRedirectUrl("//example.com/web/instructor/home"));
    }

    @Test(dataProvider = "unsupportedSchemeUrls")
    public void testIsSafeRedirectUrl_unsupportedScheme_returnsFalse(String url) {
        assertFalse(isSafeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] unsupportedSchemeUrls() {
        return new Object[][] {
                {"javascript:alert(1)"},
                {"ftp://example.com/web/instructor/home"},
        };
    }

    @Test
    public void testIsSafeRedirectUrl_nullUrl_returnsFalse() {
        assertFalse(isSafeRedirectUrl(null));
    }

    @Test(dataProvider = "malformedUrls")
    public void testIsSafeRedirectUrl_malformedUrl_returnsFalse(String url) {
        assertFalse(isSafeRedirectUrl(url));
    }

    @Test(dataProvider = "invalidRedirectUrls")
    public void testIsSafeRedirectUrl_invalidRedirectUrl_returnsFalse(String url) {
        assertFalse(isSafeRedirectUrl(url));
    }

    @Test(dataProvider = "invalidUrls")
    public void testIsSafeRedirectUrl_invalidUrl_returnsFalse(String url) {
        assertFalse(isSafeRedirectUrl(url));
    }

    @DataProvider
    private Object[][] malformedUrls() {
        return new Object[][] {
                {"https://[invalid"},
                {"example.com/invalid path"},
        };
    }

    @DataProvider
    private Object[][] invalidRedirectUrls() {
        return new Object[][] {
                {""},
                {"?query=param"},
                {"web/instructor"},
                {"https://evil.com"},
                {"//evil.com"},
        };
    }

    @DataProvider
    private Object[][] invalidUrls() {
        return new Object[][] {
                {""},
                {null},
                {"https://[invalid"},
                {"example.com/invalid path"},
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

    @Test(dataProvider = "absoluteUrls")
    public void testGetRelativeUrl_absoluteUrl_returnsRelative(String absoluteUrl, String expectedRelativeUrl) {
        assertEquals(expectedRelativeUrl, getRelativeUrl(absoluteUrl));
    }

    @DataProvider
    private Object[][] absoluteUrls() {
        return new Object[][] {
                {"https://somedomain/web/instructor/home?query=value", "/web/instructor/home?query=value"},
                {"https://somedomain/web/instructor/home", "/web/instructor/home"},
        };
    }

    @Test(dataProvider = "relativeUrls")
    public void testGetRelativeUrl_relativeUrl_returnsSame(String url) {
        assertEquals(url, getRelativeUrl(url));
    }

    @DataProvider
    private Object[][] relativeUrls() {
        return new Object[][] {
                {"/web/instructor/home"},
                {"/web/student/home?query=value"},
        };
    }

    @Test
    public void testGetRelativeUrl_emptyPath_returnsDefault() {
        String emptyPathUrl = "http://somedomain";

        assertEquals("/", getRelativeUrl(emptyPathUrl));
    }

    @Test(dataProvider = "malformedUrls")
    public void testGetRelativeUrl_malformedUrl_returnsDefault(String url) {
        assertEquals("/", getRelativeUrl(url));
    }

    @Test(dataProvider = "invalidUrls")
    public void testGetRelativeUrl_invalidUrl_returnsDefault(String url) {
        assertEquals("/", getRelativeUrl(url));
    }

    @Test(dataProvider = "invalidRelativeUrls")
    public void testGetRelativeUrl_invalidRedirectUrl_returnsDefault(String url) {
        assertEquals("/", getRelativeUrl(url));
    }

    @DataProvider
    private Object[][] invalidRelativeUrls() {
        return new Object[][] {
                {""},
                {"?query=param"},
        };
    }
}
