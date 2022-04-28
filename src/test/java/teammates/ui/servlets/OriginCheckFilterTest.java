package teammates.ui.servlets;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.BaseTestCase;
import teammates.test.MockFilterChain;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link OriginCheckFilter}.
 */
public class OriginCheckFilterTest extends BaseTestCase {

    private static final OriginCheckFilter FILTER = new OriginCheckFilter();

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockFilterChain mockFilterChain;

    private void setupMocks(String method) {
        mockRequest = new MockHttpServletRequest(method, "http://localhost:8080");
        mockRequest.setRequestedSessionId("requestedsessionid.node0");
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();
    }

    @Test
    public void allTests() throws Exception {

        ______TS("GET request without referer header will be passed");

        setupMocks(HttpGet.METHOD_NAME);

        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("GET request with invalid referer header will be blocked");

        setupMocks(HttpGet.METHOD_NAME);

        mockRequest.addHeader("referer", "thisisinvalidurl");
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("GET request with non-matching referer header will be blocked");

        setupMocks(HttpGet.METHOD_NAME);

        mockRequest.addHeader("referer", "http://localhost:9090");
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("GET request with non-matching referer header with CSRF key will be passed");

        setupMocks(HttpGet.METHOD_NAME);

        mockRequest.addHeader("referer", "http://localhost:9090");
        mockRequest.addHeader(Const.HeaderNames.CSRF_KEY, Config.CSRF_KEY);
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("POST request with non-existent CSRF token will be blocked");

        setupMocks(HttpPost.METHOD_NAME);

        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("POST request with non-existent CSRF token with CSRF key will be passed");

        setupMocks(HttpPost.METHOD_NAME);

        mockRequest.addHeader(Const.HeaderNames.CSRF_KEY, Config.CSRF_KEY);
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("POST request with invalid CSRF token will be blocked");

        setupMocks(HttpPost.METHOD_NAME);

        mockRequest.addHeader(Const.HeaderNames.CSRF_TOKEN, StringHelper.encrypt("wrongtoken"));
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        setupMocks(HttpPost.METHOD_NAME);

        mockRequest.addHeader(Const.HeaderNames.CSRF_TOKEN, "JZBCKJZXBKJBZJSDJNJKADSA");
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_FORBIDDEN, mockResponse.getStatus());

        ______TS("POST request with invalid CSRF token with CSRF key will be passed");

        setupMocks(HttpPost.METHOD_NAME);

        mockRequest.addHeader(Const.HeaderNames.CSRF_KEY, Config.CSRF_KEY);
        mockRequest.addHeader(Const.HeaderNames.CSRF_TOKEN, StringHelper.encrypt("wrongtoken"));
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        ______TS("POST request with valid CSRF token will be passed");

        setupMocks(HttpPost.METHOD_NAME);

        mockRequest.addHeader(Const.HeaderNames.CSRF_TOKEN, StringHelper.encrypt("requestedsessionid"));
        FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        if (Config.IS_DEV_SERVER) {

            ______TS("Cross-origin GET request is allowed in dev server");

            setupMocks(HttpGet.METHOD_NAME);

            mockRequest.addHeader("referer", Config.APP_FRONTEND_URL);
            FILTER.doFilter(mockRequest, mockResponse, mockFilterChain);
            assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

        }

    }

}
