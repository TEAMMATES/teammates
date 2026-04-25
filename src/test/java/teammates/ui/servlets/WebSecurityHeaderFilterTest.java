package teammates.ui.servlets;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockFilterChain;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link WebSecurityHeaderFilter}.
 */
public class WebSecurityHeaderFilterTest extends BaseTestCase {

    private static final WebSecurityHeaderFilter FILTER = new WebSecurityHeaderFilter();

    @Test
    public void doFilter_setsDocumentSecurityHeaders() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpGet.METHOD_NAME, "http://localhost:8080/web/home");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        FILTER.doFilter(request, response, filterChain);

        assertTrue(filterChain.wasInvoked());
        assertEquals(response.getHeader("Content-Security-Policy"), SecurityHeaders.CONTENT_SECURITY_POLICY);
        assertEquals(response.getHeader("Strict-Transport-Security"), SecurityHeaders.STRICT_TRANSPORT_SECURITY);
        assertEquals(response.getHeader("X-Content-Type-Options"), SecurityHeaders.X_CONTENT_TYPE_OPTIONS);
        assertEquals(response.getHeader("Referrer-Policy"), SecurityHeaders.REFERRER_POLICY);
        assertEquals(response.getHeader("Permissions-Policy"), SecurityHeaders.PERMISSIONS_POLICY);
        assertEquals(response.getHeader("X-Frame-Options"), SecurityHeaders.X_FRAME_OPTIONS);
        assertEquals(response.getHeader("X-XSS-Protection"), SecurityHeaders.X_XSS_PROTECTION);
    }

}
