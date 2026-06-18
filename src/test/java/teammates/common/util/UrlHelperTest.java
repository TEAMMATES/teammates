package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link UrlHelper}.
 */
public class UrlHelperTest extends BaseTestCase {

    private boolean isSafeRedirectUrl(String url) {
        return UrlHelper.isSafeRedirectUrl(url);
    }

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

    @Test
    public void testIsSafeRedirectUrl_externalUrl_returnsFalse() {
        assertFalse(isSafeRedirectUrl("https://example.com/web/instructor/home"));
        assertFalse(isSafeRedirectUrl("https://evil.example.com"));
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

    @Test
    public void testIsSafeRedirectUrl_unsupportedScheme_returnsFalse() {
        assertFalse(isSafeRedirectUrl("javascript:alert(1)"));
        assertFalse(isSafeRedirectUrl("ftp://example.com/web/instructor/home"));
    }

    @Test
    public void testIsSafeRedirectUrl_invalidUrl_returnsFalse() {
        assertFalse(isSafeRedirectUrl(null));
        assertFalse(isSafeRedirectUrl("https://[invalid"));
        assertFalse(isSafeRedirectUrl("web/instructor/home"));
    }

}
