package teammates.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class for monitoring performance of database and API operations.
 * Provides automatic performance logging with configurable thresholds for slow operations.
 */
public final class PerformanceMonitor {

    private static final Logger log = Logger.getLogger();
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000;
    private static final long SLOW_API_THRESHOLD_MS = 2000;

    // Performance categories for better categorization
    public static final String CATEGORY_DB = "DB";
    public static final String CATEGORY_API = "API";
    public static final String CATEGORY_CACHE = "CACHE";
    public static final String CATEGORY_SEARCH = "SEARCH";
    public static final String CATEGORY_EMAIL = "EMAIL";

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
     * Monitors an operation with a custom threshold.
     * Useful for operations that have specific performance requirements.
     *
     * @param category Category of the operation (e.g., "DB", "API", "CACHE")
     * @param operationName Name of the operation
     * @param operation The operation to monitor
     * @param customThresholdMs Custom threshold in milliseconds
     * @return Result of the operation
     */
    public static <T> T monitorOperationWithThreshold(String category, String operationName,
                                                       Supplier<T> operation, long customThresholdMs) {
        return monitor(category, operationName, operation, customThresholdMs);
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
            metrics.put("status", "ERROR");
            metrics.put("error", error.getClass().getSimpleName());
            metrics.put("errorMessage", error.getMessage());
            
            String errorLog = String.format(
                "[%s ERROR] %s failed after %dms%n" +
                "  Category: %s%n" +
                "  Operation: %s%n" +
                "  Duration: %dms%n" +
                "  Error Type: %s%n" +
                "  Error Message: %s",
                category, operationName, durationMs,
                category, operationName, durationMs,
                error.getClass().getSimpleName(),
                error.getMessage()
            );
            log.warning(errorLog, error);
            
        } else if (durationMs > threshold) {
            metrics.put("status", "SLOW");
            metrics.put("exceedsThresholdBy", durationMs - threshold);
            
            String slowLog = String.format(
                "[SLOW %s] %s took %dms (threshold: %dms)%n" +
                "  Exceeded by: %dms%n" +
                "  Category: %s%n" +
                "  Operation: %s",
                category, operationName, durationMs, threshold,
                (durationMs - threshold),
                category, operationName
            );
            log.warning(slowLog);
            
        } else {
            metrics.put("status", "OK");
            log.info(String.format("[%s] %s completed in %dms",
                    category, operationName, durationMs));
        }

        log.performance(operationName, durationMs, metrics);
    }
}
