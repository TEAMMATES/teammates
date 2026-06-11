package teammates.common.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import teammates.common.exception.DeadlineExceededException;

/**
 * Stores the information of the current HTTP request.
 */
public final class RequestTracer {

    private static final ThreadLocal<RequestTrace> THREAD_LOCAL = new ThreadLocal<>();

    private RequestTracer() {
        // utility class
    }

    /**
     * Returns the request ID of the current request.
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
    private static long getRemainingTimeMillis() {
        RequestTrace trace = THREAD_LOCAL.get();
        if (trace == null) {
            return 1L;
        }
        return trace.timeoutTimestamp - Instant.now().toEpochMilli();
    }

    /**
     * Throws {@link DeadlineExceededException} if the current thread has exceeded
     * the limit for serving request.
     */
    public static void checkRemainingTime() {
        long remainingTime = getRemainingTimeMillis();
        if (remainingTime < 0) {
            throw new DeadlineExceededException();
        }
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
    public static void init(String requestId, int timeoutInSeconds) {
        THREAD_LOCAL.set(new RequestTrace(requestId, timeoutInSeconds));
    }

    private static final class RequestTrace {
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
