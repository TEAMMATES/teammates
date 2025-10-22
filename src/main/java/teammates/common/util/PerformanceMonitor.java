package teammates.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class for monitoring performance of database and API operations.
 */
public final class PerformanceMonitor {

    private static final Logger log = Logger.getLogger();
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000;
    private static final long SLOW_API_THRESHOLD_MS = 2000;

    private PerformanceMonitor() {
    }

    /**
     * Monitors a database operation (query, update, delete).
     *
     * @param operationName Name of the DB operation
     * @param operation The operation to monitor
     * @return Result of the operation
     */
    public static <T> T monitorDatabaseOperation(String operationName, Supplier<T> operation) {
        return monitor("DB", operationName, operation, SLOW_QUERY_THRESHOLD_MS);
    }

    /**
     * Monitors a database operation that returns void.
     */
    public static void monitorDatabaseOperationVoid(String operationName, Runnable operation) {
        monitor("DB", operationName, () -> {
            operation.run();
            return null;
        }, SLOW_QUERY_THRESHOLD_MS);
    }

    /**
     * Monitors an API endpoint operation.
     *
     * @param operationName Name of the API action
     * @param operation The operation to monitor
     * @return Result of the operation
     */
    public static <T> T monitorApiOperation(String operationName, Supplier<T> operation) {
        return monitor("API", operationName, operation, SLOW_API_THRESHOLD_MS);
    }

    /**
     * Core monitoring logic.
     */
    private static <T> T monitor(String category, String operationName,
                                 Supplier<T> operation, long threshold) {
        long startTime = System.nanoTime();
        T result = null;
        Throwable error = null;

        try {
            result = operation.get();
            return result;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logPerformance(category, operationName, durationMs, threshold, result, error);
        }
    }

    /**
     * Logs performance metrics with appropriate severity level.
     */
    private static void logPerformance(String category, String operationName,
                                       long durationMs, long threshold,
                                       Object result, Throwable error) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("category", category);
        metrics.put("operation", operationName);
        metrics.put("durationMs", durationMs);
        metrics.put("threshold", threshold);

        if (result != null) {
            metrics.put("resultType", result.getClass().getSimpleName());
        }

        if (error != null) {
            metrics.put("error", error.getClass().getSimpleName());
            metrics.put("errorMessage", error.getMessage());
            log.warning(String.format("[%s ERROR] %s failed after %dms",
                    category, operationName, durationMs), error);
        } else if (durationMs > threshold) {
            log.warning(String.format("[SLOW %s] %s took %dms (threshold: %dms)",
                    category, operationName, durationMs, threshold));
        } else {
            log.info(String.format("[%s] %s completed in %dms",
                    category, operationName, durationMs));
        }

        log.performance(operationName, durationMs, metrics);
    }
}
