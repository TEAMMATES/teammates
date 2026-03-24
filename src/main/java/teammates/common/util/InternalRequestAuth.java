package teammates.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Helper for authenticating internal requests (cron and worker) via bearer token.
 */
public final class InternalRequestAuth {

    private static final String BEARER_PREFIX = "Bearer ";

    private InternalRequestAuth() {
        // utility class
    }

    /**
     * Returns true if the request has a valid bearer token for cron or worker authentication
     * and the request URI targets a cron ({@code /auto/*}) or worker ({@code /worker/*}) path
     * (including the application context path prefix when present).
     */
    public static boolean isTrustedCronOrWorkerRequest(HttpServletRequest req) {
        String secret = Config.CRON_AND_WORKER_SECRET;
        if (secret == null || secret.trim().isEmpty()) {
            return false;
        }
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            return false;
        }
        String token = auth.substring(BEARER_PREFIX.length());
        if (token.trim().isEmpty()) {
            return false;
        }
        if (!MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),
                secret.getBytes(StandardCharsets.UTF_8))) {
            return false;
        }
        return isCronOrWorkerPath(req);
    }

    /**
     * Returns true if the request URI targets a cron ({@code /auto/*}) path.
     */
    public static boolean isCronRequestPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) {
            return false;
        }
        String context = req.getContextPath();
        if (context == null) {
            context = "";
        }
        String cronPrefix = context + "/auto/";
        return uri.startsWith(cronPrefix) || uri.equals(context + "/auto");
    }

    /**
     * Returns true if the request URI targets a worker ({@code /worker/*}) path.
     */
    public static boolean isWorkerRequestPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) {
            return false;
        }
        String context = req.getContextPath();
        if (context == null) {
            context = "";
        }
        String workerPrefix = context + "/worker/";
        return uri.startsWith(workerPrefix) || uri.equals(context + "/worker");
    }

    private static boolean isCronOrWorkerPath(HttpServletRequest req) {
        return isCronRequestPath(req) || isWorkerRequestPath(req);
    }
}
