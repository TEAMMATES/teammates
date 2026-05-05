package teammates.ui.servlets;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.hibernate.HibernateException;
import org.testng.annotations.Test;

import teammates.common.exception.DeadlineExceededException;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletResponse;
import teammates.ui.exception.ActionMappingException;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link WebApiServletExceptionHandler}.
 */
public class WebApiServletExceptionHandlerTest extends BaseTestCase {

    private static int handleException(MockHttpServletResponse resp, Throwable t) throws IOException {
        return WebApiServletExceptionHandler.handleException(resp, t);
    }

    @Test
    public void testActionMappingException_notFound() throws Exception {
        ______TS("404 for unknown resource");

        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_NOT_FOUND,
                handleException(resp, new ActionMappingException("not found", HttpStatus.SC_NOT_FOUND)));
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testActionMappingException_methodNotAllowed() throws Exception {
        ______TS("405 when HTTP method does not match");

        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED,
                handleException(resp, new ActionMappingException("method not allowed",
                        HttpStatus.SC_METHOD_NOT_ALLOWED)));
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, resp.getStatus());
    }

    @Test
    public void testInvalidHttpParameterException_badRequest() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST,
                handleException(resp, new InvalidHttpParameterException("bad param")));
        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testInvalidHttpRequestBodyException_badRequest() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST,
                handleException(resp, new InvalidHttpRequestBodyException("bad body")));
        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testUnauthorizedAccessException_forbidden() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_FORBIDDEN,
                handleException(resp, new UnauthorizedAccessException("no access")));
        assertEquals(HttpStatus.SC_FORBIDDEN, resp.getStatus());
    }

    @Test
    public void testEntityNotFoundException_notFound() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_NOT_FOUND,
                handleException(resp, new EntityNotFoundException("missing")));
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testInvalidOperationException_conflict() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_CONFLICT,
                handleException(resp, new InvalidOperationException("conflict")));
        assertEquals(HttpStatus.SC_CONFLICT, resp.getStatus());
    }

    @Test
    public void testDeadlineExceededException_gatewayTimeout() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT,
                handleException(resp, new DeadlineExceededException()));
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, resp.getStatus());
    }

    @Test
    public void testHibernateException_internalServerError() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(resp, new HibernateException("db error")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void testNullPointerException_internalServerError() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(resp, new NullPointerException("npe")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void testAssertionError_internalServerError() throws Exception {
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(resp, new AssertionError("assert")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

}
