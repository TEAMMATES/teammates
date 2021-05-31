package teammates.common.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
     * Returns the remaining time (in millis) until the current request times out.
     */
    public static long getRemainingTimeMillis() {
        RequestTrace trace = THREAD_LOCAL.get();
        if (trace == null) {
            return -1L;
        }
        return trace.timeoutTimestamp - Instant.now().toEpochMilli();
    }

    /**
     * Returns the remaining time (in millis) until the current request times out.
     */
    public static long getTimeElapsedMillis() {
        RequestTrace trace = THREAD_LOCAL.get();
        if (trace == null) {
            return -1L;
        }
        return Instant.now().toEpochMilli() - trace.initTimestamp;
    }

    /**
     * Initializes the request with an ID and the timeout value (in seconds).
     */
    public static void init(String requestIdParam, int timeoutInSeconds) {
        String requestId = requestIdParam;
        if (requestId == null) {
            requestId = RandomStringUtils.randomAlphanumeric(32);
        }
        THREAD_LOCAL.set(new RequestTrace(requestId, timeoutInSeconds));
    }

    private static class RequestTrace {
        private final String requestId;
        private final long initTimestamp;
        private final long timeoutTimestamp;

        private RequestTrace(String requestId, int timeoutInSeconds) {
            this.requestId = requestId;
            this.initTimestamp = Instant.now().toEpochMilli();
            this.timeoutTimestamp = Instant.now().plus(timeoutInSeconds, ChronoUnit.SECONDS).toEpochMilli();
        }
    }

}
