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

    @Test
    public void testIsSafeRedirectUrl_relativeUrl_returnsTrue() {
        String url = "/web/instructor/home";

        assertTrue(isSafeRedirectUrl(url));
    }

    @Test
    public void testIsSafeRedirectUrl_configuredFrontendUrl_returnsTrue() {
        String url = Config.APP_FRONTEND_URL + "/web/instructor/home";

        assertTrue(isSafeRedirectUrl(url));
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

    @DataProvider
    private Object[][] malformedUrls() {
        return new Object[][] {
                {"https://[invalid"},
                {"web/instructor/home"},
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

    @Test
    public void testGetRelativeUrl_absoluteUrl_returnsRelative() {
        String absoluteUrl = "http://somedomain/web/instructor/home";
        String expectedRelativeUrl = "/web/instructor/home";

        assertEquals(expectedRelativeUrl, getRelativeUrl(absoluteUrl));
    }

    @Test
    public void testGetRelativeUrl_relativeUrl_returnsSame() {
        String relativeUrl = "/web/instructor/home";

        assertEquals(relativeUrl, getRelativeUrl(relativeUrl));
    }

    @Test
    public void testGetRelativeUrl_emptyPath_returnsDefault() {
        String emptyPathUrl = "http://somedomain";

        assertEquals("/", getRelativeUrl(emptyPathUrl));
    }

    @Test
    public void testGetRelativeUrl_malformedUrl_returnsDefault() {
        String malformedUrl = "http://[invalid";

        assertEquals("/", getRelativeUrl(malformedUrl));
    }
}
