package teammates.common.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Stores the information of the current HTTP request.
 */
public final class RequestTracer {

    private static final ThreadLocal<RequestTrace> THREAD_LOCAL = new ThreadLocal<>();

    private RequestTracer() {
        // utility class
    }

    /**
     * Returns the ID of the current request.
     */
    public static String getRequestId() {
        RequestTrace trace = THREAD_LOCAL.get();
        if (trace == null) {
            return null;
        }
        return trace.requestId;
    }

    /**
     * Initializes the request with an ID.
     */
    public static void init(String requestIdParam) {
        String requestId = requestIdParam;
        if (requestId == null) {
            requestId = RandomStringUtils.randomAlphanumeric(32);
        }
        THREAD_LOCAL.set(new RequestTrace(requestId));
    }

    private static class RequestTrace {
        private final String requestId;

        private RequestTrace(String requestId) {
            this.requestId = requestId;
        }
    }

}
