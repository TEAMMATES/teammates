package teammates.common.util;

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
     * Returns true if the request has a valid bearer token for cron or worker authentication.
     * Used for /auto/* (cron) and /worker/* (worker) endpoints.
     */
    public static boolean isTrustedCronOrWorkerRequest(HttpServletRequest req) {
        String secret = Config.CRON_AND_WORKER_SECRET;
        if (secret == null) {
            return false;
        }
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith(BEARER_PREFIX)) {
            String token = auth.substring(BEARER_PREFIX.length());
            return token.equals(secret);
        }
        return false;
    }
}
