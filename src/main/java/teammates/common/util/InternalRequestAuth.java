package teammates.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Helper for authenticating internal requests (cron and worker) via bearer token.
 */
public final class InternalRequestAuth {

    private InternalRequestAuth() {
        // utility class
    }

    /**
     * Returns true if {@code secret} is non-empty and has no leading or trailing whitespace.
     * Misconfigured secrets must not be normalized via trimming — callers should fail closed instead.
     */
    public static boolean isCronAndWorkerSecretWellFormed(String secret) {
        return secret != null && !secret.isEmpty() && secret.equals(secret.trim());
    }

    /**
     * Returns true if the request has a valid bearer token for cron or worker authentication
     * and the request URI targets a cron ({@code /auto/*}) or worker ({@code /worker/*}) path
     * (including the application context path prefix when present).
     */
    public static boolean isTrustedCronOrWorkerRequest(HttpServletRequest req) {
        return isTrustedCronOrWorkerRequest(req, Config.CRON_AND_WORKER_SECRET_BYTES);
    }

    /**
     * Same as {@link #isTrustedCronOrWorkerRequest(HttpServletRequest)} but with an explicit expected secret
     * (production uses {@link Config#CRON_AND_WORKER_SECRET_BYTES}). Package-private for deterministic unit tests.
     */
    static boolean isTrustedCronOrWorkerRequest(HttpServletRequest req, String secret) {
        if (!isCronAndWorkerSecretWellFormed(secret)) {
            return false;
        }
        return isTrustedCronOrWorkerRequest(req, secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Package-private for deterministic unit tests and for sharing logic with {@link #isTrustedCronOrWorkerRequest(
     * HttpServletRequest, String)}.
     */
    static boolean isTrustedCronOrWorkerRequest(HttpServletRequest req, byte[] secretBytes) {
        if (req.getContextPath() == null) {
            return false;
        }
        if (secretBytes == null || secretBytes.length == 0) {
            return false;
        }
        String token = HttpRequestHelper.parseBearerTokenFromAuthorizationHeader(
                req.getHeader(Const.HeaderNames.AUTHORIZATION_KEY));
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (!MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), secretBytes)) {
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
