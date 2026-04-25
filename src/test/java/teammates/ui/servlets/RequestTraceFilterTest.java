package teammates.ui.servlets;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockFilterChain;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link RequestTraceFilter}.
 */
public class RequestTraceFilterTest extends BaseTestCase {

    private static final RequestTraceFilter FILTER = new RequestTraceFilter();

    @Test
    public void doFilter_setsCommonSecurityHeadersForApiRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpGet.METHOD_NAME, "http://localhost:8080/webapi/course");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        FILTER.doFilter(request, response, filterChain);

        assertTrue(filterChain.wasInvoked());
        assertEquals(response.getHeader("Strict-Transport-Security"), SecurityHeaders.STRICT_TRANSPORT_SECURITY);
        assertEquals(response.getHeader("X-Content-Type-Options"), SecurityHeaders.X_CONTENT_TYPE_OPTIONS);
        assertEquals(response.getHeader("Referrer-Policy"), SecurityHeaders.REFERRER_POLICY);
        assertEquals(response.getHeader("Permissions-Policy"), SecurityHeaders.PERMISSIONS_POLICY);
        assertEquals(response.getHeader("Cache-Control"), "no-store");
        assertEquals(response.getHeader("Pragma"), "no-cache");
        assertNull(response.getHeader("Content-Security-Policy"));
    }

}
