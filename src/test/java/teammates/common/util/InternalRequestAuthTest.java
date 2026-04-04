package teammates.common.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link InternalRequestAuth}.
 */
public class InternalRequestAuthTest extends BaseTestCase {

    /** Fixed secret so bearer-trust tests do not depend on build.properties or CI config. */
    private static final String TEST_CRON_WORKER_SECRET = "test-cron-worker-secret-for-unit-tests";

    private static HttpServletRequest mockRequest(String requestUri, String contextPath, String authorizationHeader) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(requestUri);
        when(req.getContextPath()).thenReturn(contextPath);
        when(req.getHeader("Authorization")).thenReturn(authorizationHeader);
        return req;
    }

    /** Root servlet context: use empty string, not null, for trusted-auth tests. */
    private static HttpServletRequest mockTrustedRootContextRequest(String requestUri, String authorizationHeader) {
        return mockRequest(requestUri, "", authorizationHeader);
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
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/sync", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_bearerSchemeCaseInsensitive() {
        HttpServletRequest lower = mockTrustedRootContextRequest("/auto/sync", "bearer " + TEST_CRON_WORKER_SECRET);
        HttpServletRequest upper = mockTrustedRootContextRequest("/auto/sync", "BEARER " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(lower, TEST_CRON_WORKER_SECRET));
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(upper, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_correctToken_workerPath() {
        HttpServletRequest req = mockTrustedRootContextRequest("/worker/execute", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_wrongToken() {
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/sync", "Bearer " + TEST_CRON_WORKER_SECRET + "-wrong");
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_correctToken_wrongPath() {
        HttpServletRequest req = mockTrustedRootContextRequest("/webapi/something", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_withContextPath_validBearer() {
        HttpServletRequest req = mockRequest("/teammates/auto/run", "/teammates", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_missingAuthorization() {
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/sync", null);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_nonBearerAuthorization() {
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/sync", "Basic " + TEST_CRON_WORKER_SECRET);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_bearerEmptyToken() {
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/sync", "Bearer ");
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_bearerTokenTrimmingAccepted() {
        String header = "Bearer   " + TEST_CRON_WORKER_SECRET + "   ";
        HttpServletRequest cronReq = mockTrustedRootContextRequest("/auto/jobs", header);
        HttpServletRequest workerReq = mockTrustedRootContextRequest("/worker/task", header);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(cronReq, TEST_CRON_WORKER_SECRET));
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(workerReq, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsCronAndWorkerSecretWellFormed_rejectsSurroundingWhitespace() {
        assertFalse(InternalRequestAuth.isCronAndWorkerSecretWellFormed("  " + TEST_CRON_WORKER_SECRET + "  "));
        assertTrue(InternalRequestAuth.isCronAndWorkerSecretWellFormed(TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_rootContextPath_emptyString() {
        HttpServletRequest req = mockRequest("/auto/reminder", "", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_nullContextPath_failClosed() {
        HttpServletRequest req = mockRequest("/auto/sync", null, "Bearer " + TEST_CRON_WORKER_SECRET);
        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testTrustedCronVsWorkerPath_separation() {
        HttpServletRequest cronReq = mockTrustedRootContextRequest("/auto/cronJob", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(cronReq, TEST_CRON_WORKER_SECRET));
        assertFalse(InternalRequestAuth.isWorkerRequestPath(cronReq));

        HttpServletRequest workerReq = mockTrustedRootContextRequest("/worker/run", "Bearer " + TEST_CRON_WORKER_SECRET);
        assertTrue(InternalRequestAuth.isTrustedCronOrWorkerRequest(workerReq, TEST_CRON_WORKER_SECRET));
        assertTrue(InternalRequestAuth.isWorkerRequestPath(workerReq));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_nullRequestUri() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(null);
        when(req.getContextPath()).thenReturn("");
        when(req.getHeader("Authorization")).thenReturn("Bearer " + TEST_CRON_WORKER_SECRET);

        assertFalse(InternalRequestAuth.isTrustedCronOrWorkerRequest(req, TEST_CRON_WORKER_SECRET));
    }

    @Test
    public void testIsTrustedCronOrWorkerRequest_publicApiMatchesExplicitConfigSecret() {
        HttpServletRequest req = mockTrustedRootContextRequest("/auto/x", null);
        assertEquals(
                InternalRequestAuth.isTrustedCronOrWorkerRequest(req),
                InternalRequestAuth.isTrustedCronOrWorkerRequest(req, Config.CRON_AND_WORKER_SECRET));
    }

}
