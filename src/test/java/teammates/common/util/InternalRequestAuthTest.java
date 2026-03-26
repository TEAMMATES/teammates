package teammates.common.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link InternalRequestAuth}.
 */
public class InternalRequestAuthTest extends BaseTestCase {

    private static String configuredSecret;

    @BeforeClass
    public static void verifyCronWorkerSecretConfigured() {
        configuredSecret = Config.CRON_AND_WORKER_SECRET;
        if (configuredSecret == null || configuredSecret.trim().isEmpty()) {
            throw new SkipException(
                    "InternalRequestAuth tests require app.cron.and.worker.secret in build.properties (see build.template.properties).");
        }
    }

    private static HttpServletRequest mockRequest(String requestUri, String contextPath, String authorizationHeader) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(requestUri);
        when(req.getContextPath()).thenReturn(contextPath);
        when(req.getHeader("Authorization")).thenReturn(authorizationHeader);
        return req;
    }

    @Test
    public void testIsCronRequestPath_noContextPath() {
        ______TS("cron path prefix");

        HttpServletRequest req = mockRequest("/auto/jobs/foo", null, null);
        assertTrue(InternalRequestAuth.isCronRequestPath(req));

        ______TS("cron path exact /auto");

        req = mockRequest("/auto", null, null);
        assertTrue(InternalRequestAuth.isCronRequestPath(req));

        ______TS("non-cron path");

        req = mockRequest("/worker/task", null, null);
        assertFalse(InternalRequestAuth.isCronRequestPath(req));
    }

    @Test
    public void testIsCronRequestPath_withContextPath() {
        ______TS("cron path with servlet context prefix");

        HttpServletRequest req = mockRequest("/teammates/auto/jobs", "/teammates", null);
        assertTrue(InternalRequestAuth.isCronRequestPath(req));

        ______TS("cron base path with context");

        req = mockRequest("/teammates/auto", "/teammates", null);
        assertTrue(InternalRequestAuth.isCronRequestPath(req));

        ______TS("URI missing context prefix is not cron");

        req = mockRequest("/auto/jobs", "/teammates", null);
        assertFalse(InternalRequestAuth.isCronRequestPath(req));
    }

    @Test
    public void testIsWorkerRequestPath_noContextPath() {
        ______TS("worker path prefix");

        HttpServletRequest req = mockRequest("/worker/task", null, null);
        assertTrue(InternalRequestAuth.isWorkerRequestPath(req));

        ______TS("worker path exact /worker");

        req = mockRequest("/worker", null, null);
        assertTrue(InternalRequestAuth.isWorkerRequestPath(req));

        ______TS("non-worker path");

        req = mockRequest("/auto/jobs", null, null);
        assertFalse(InternalRequestAuth.isWorkerRequestPath(req));
    }

    @Test
    public void testIsWorkerRequestPath_withContextPath() {
        ______TS("worker path with servlet context prefix");

        HttpServletRequest req = mockRequest("/teammates/worker/run", "/teammates", null);
        assertTrue(InternalRequestAuth.isWorkerRequestPath(req));

        ______TS("worker base path with context");

        req = mockRequest("/teammates/worker", "/teammates", null);
        assertTrue(InternalRequestAuth.isWorkerRequestPath(req));

        ______TS("URI missing context prefix is not worker");

        req = mockRequest("/worker/run", "/teammates", null);
        assertFalse(InternalRequestAuth.isWorkerRequestPath(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_correctToken_cronPath() {
        HttpServletRequest req = mockRequest("/auto/sync", null, "Bearer " + configuredSecret);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_correctToken_workerPath() {
        HttpServletRequest req = mockRequest("/worker/execute", null, "Bearer " + configuredSecret);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_wrongToken() {
        HttpServletRequest req = mockRequest("/auto/sync", null, "Bearer " + configuredSecret + "-wrong");
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_correctToken_wrongPath() {
        HttpServletRequest req = mockRequest("/webapi/something", null, "Bearer " + configuredSecret);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_withContextPath_validBearer() {
        HttpServletRequest req = mockRequest("/teammates/auto/run", "/teammates", "Bearer " + configuredSecret);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_missingAuthorization() {
        HttpServletRequest req = mockRequest("/auto/sync", null, null);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_nonBearerAuthorization() {
        HttpServletRequest req = mockRequest("/auto/sync", null, "Basic " + configuredSecret);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_bearerEmptyToken() {
        HttpServletRequest req = mockRequest("/auto/sync", null, "Bearer ");
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_nullRequestUri() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(null);
        when(req.getContextPath()).thenReturn("");
        when(req.getHeader("Authorization")).thenReturn("Bearer " + configuredSecret);

        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req));
    }

}
