package teammates.ui.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.hibernate.HibernateException;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import teammates.common.exception.DeadlineExceededException;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.InternalRequestAuth;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.ActionMappingException;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.UnauthorizedAccessException;

/**
 * SUT: {@link WebApiServletExceptionHandler}.
 */
public class WebApiServletExceptionHandlerTest extends BaseTestCase {

    private static final String SAMPLE_WORKER_URI = TaskQueue.SEND_EMAIL_WORKER_URL;
    private static final String SAMPLE_WORKER_URI_ALT = TaskQueue.URI_PREFIX + "/otherWorker";

    private static int handleException(MockHttpServletRequest req, MockHttpServletResponse resp, Throwable t)
            throws IOException {
        return WebApiServletExceptionHandler.handleException(req, resp, t);
    }

    @Test
    public void testActionMappingException_notFound() throws Exception {
        ______TS("404 for unknown resource");

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/unknown");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_NOT_FOUND,
                handleException(req, resp, new ActionMappingException("not found", HttpStatus.SC_NOT_FOUND)));
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testActionMappingException_methodNotAllowed() throws Exception {
        ______TS("405 when HTTP method does not match");

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webapi/course");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED,
                handleException(req, resp, new ActionMappingException("method not allowed",
                        HttpStatus.SC_METHOD_NOT_ALLOWED)));
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, resp.getStatus());
    }

    @Test
    public void testInvalidHttpParameterException_badRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST,
                handleException(req, resp, new InvalidHttpParameterException("bad param")));
        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testInvalidHttpRequestBodyException_badRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST,
                handleException(req, resp, new InvalidHttpRequestBodyException("bad body")));
        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testUnauthorizedAccessException_forbidden() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_FORBIDDEN,
                handleException(req, resp, new UnauthorizedAccessException("no access")));
        assertEquals(HttpStatus.SC_FORBIDDEN, resp.getStatus());
    }

    @Test
    public void testEntityNotFoundException_notFound() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_NOT_FOUND,
                handleException(req, resp, new EntityNotFoundException("missing")));
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testInvalidOperationException_conflict() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_CONFLICT,
                handleException(req, resp, new InvalidOperationException("conflict")));
        assertEquals(HttpStatus.SC_CONFLICT, resp.getStatus());
    }

    @Test
    public void testDeadlineExceededException_gatewayTimeout() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT,
                handleException(req, resp, new DeadlineExceededException()));
        assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, resp.getStatus());
    }

    @Test
    public void testHibernateException_internalServerError() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(req, resp, new HibernateException("db error")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void testNullPointerException_internalServerError() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(req, resp, new NullPointerException("npe")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void testAssertionError_internalServerError() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/webapi/x");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                handleException(req, resp, new AssertionError("assert")));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
    }

    @Test
    public void testTrustedWorker_actionMappingErrors_accepted() throws Exception {
        try (MockedStatic<InternalRequestAuth> internalAuth = mockStatic(InternalRequestAuth.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            internalAuth.when(() -> InternalRequestAuth.isTrustedCronOrWorkerRequest(any())).thenReturn(true);

            ______TS("Invalid action mapping on worker URL returns 202");

            MockHttpServletRequest req = new MockHttpServletRequest("GET", TaskQueue.URI_PREFIX + "/nonexistent");
            MockHttpServletResponse resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_NOT_FOUND,
                    handleException(req, resp, new ActionMappingException("not found", HttpStatus.SC_NOT_FOUND)));
            assertEquals(HttpStatus.SC_ACCEPTED, resp.getStatus());

            ______TS("Method not allowed on worker URL returns 202");

            req = new MockHttpServletRequest("GET", SAMPLE_WORKER_URI);
            resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, handleException(req, resp,
                    new ActionMappingException("method not allowed", HttpStatus.SC_METHOD_NOT_ALLOWED)));
            assertEquals(HttpStatus.SC_ACCEPTED, resp.getStatus());

            ______TS("Bad request on worker URL returns 202");

            req = new MockHttpServletRequest("GET", SAMPLE_WORKER_URI_ALT);
            resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_BAD_REQUEST,
                    handleException(req, resp, new InvalidHttpParameterException("bad")));
            assertEquals(HttpStatus.SC_ACCEPTED, resp.getStatus());
        }
    }

    @Test
    public void testTrustedWorker_unauthorizedAccess_stillForbidden() throws Exception {
        try (MockedStatic<InternalRequestAuth> internalAuth = mockStatic(InternalRequestAuth.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            internalAuth.when(() -> InternalRequestAuth.isTrustedCronOrWorkerRequest(any())).thenReturn(true);

            MockHttpServletRequest req = new MockHttpServletRequest("GET", SAMPLE_WORKER_URI);
            MockHttpServletResponse resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_FORBIDDEN,
                    handleException(req, resp, new UnauthorizedAccessException("no access")));
            assertEquals(HttpStatus.SC_FORBIDDEN, resp.getStatus());
        }
    }

    @Test
    public void testTrustedWorker_entityNotFound_stillNotFound() throws Exception {
        try (MockedStatic<InternalRequestAuth> internalAuth = mockStatic(InternalRequestAuth.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            internalAuth.when(() -> InternalRequestAuth.isTrustedCronOrWorkerRequest(any())).thenReturn(true);

            MockHttpServletRequest req = new MockHttpServletRequest("GET", SAMPLE_WORKER_URI);
            MockHttpServletResponse resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_NOT_FOUND,
                    handleException(req, resp, new EntityNotFoundException("missing")));
            assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatus());
        }
    }

    @Test
    public void testTrustedWorker_unexpectedThrowable_internalServerError() throws Exception {
        try (MockedStatic<InternalRequestAuth> internalAuth = mockStatic(InternalRequestAuth.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            internalAuth.when(() -> InternalRequestAuth.isTrustedCronOrWorkerRequest(any())).thenReturn(true);

            MockHttpServletRequest req = new MockHttpServletRequest("GET", SAMPLE_WORKER_URI);
            MockHttpServletResponse resp = new MockHttpServletResponse();

            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    handleException(req, resp, new RuntimeException("boom")));
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, resp.getStatus());
        }
    }

}
