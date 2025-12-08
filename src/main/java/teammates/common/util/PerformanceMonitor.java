package teammates.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class for monitoring performance of database and API operations.
 * Provides automatic performance logging with configurable thresholds for slow
 * operations.
 */
public final class PerformanceMonitor {

    /** Performance categories for better categorization. */
    public static final String CATEGORY_DB = "DB";
    /** API category constant. */
    public static final String CATEGORY_API = "API";
    /** Cache category constant. */
    public static final String CATEGORY_CACHE = "CACHE";
    /** Search category constant. */
    public static final String CATEGORY_SEARCH = "SEARCH";
    /** Email category constant. */
    public static final String CATEGORY_EMAIL = "EMAIL";

    private static final Logger log = Logger.getLogger();
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000;
    private static final long SLOW_API_THRESHOLD_MS = 2000;

    private PerformanceMonitor() {
    }

    /**
     * Monitors a database operation (query, update, delete).
     *
     * @param operationName Name of the DB operation
     * @param operation     The operation to monitor
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
     * Functional interface for void operations that can throw checked exceptions.
     *
     * @param <E> the type of exception that may be thrown
     */
    @FunctionalInterface
    public interface ThrowingRunnable<E extends Exception> {
        /**
         * Runs the operation, potentially throwing an exception.
         *
         * @throws E if unable to run
         */
        void run() throws E;
    }

    /**
     * Monitors a database operation that returns void and may throw checked
     * exceptions.
     *
     * @param operationName Name of the DB operation
     * @param operation     The operation to monitor
     * @param <E>           The exception type
     * @throws E if the operation throws an exception
     */
    public static <E extends Exception> void monitorDatabaseOperationVoidWithException(
            String operationName, ThrowingRunnable<E> operation) throws E {
        monitorDatabaseOperationWithException(operationName, () -> {
            operation.run();
            return null;
        });
    }

    /**
     * Monitors a database operation that may throw checked exceptions.
     *
     * @param operationName Name of the DB operation
     * @param operation     The operation to monitor
     * @param <T>           The return type
     * @param <E>           The exception type
     * @return Result of the operation
     * @throws E if the operation throws an exception
     */
    public static <T, E extends Exception> T monitorDatabaseOperationWithException(
            String operationName, ThrowingSupplier<T, E> operation) throws E {
        long startTime = System.nanoTime();
        T result = null;
        Exception error = null;

        try {
            result = operation.get();
            return result;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logPerformance("DB", operationName, durationMs, SLOW_QUERY_THRESHOLD_MS, result, error);
        }
    }

    /**
     * Monitors an API endpoint operation.
     *
     * @param operationName Name of the API action
     * @param operation     The operation to monitor
     * @return Result of the operation
     */
    public static <T> T monitorApiOperation(String operationName, Supplier<T> operation) {
        return monitor("API", operationName, operation, SLOW_API_THRESHOLD_MS);
    }

    /**
     * Functional interface for operations that can throw checked exceptions.
     *
     * @param <T> the type of the result
     * @param <E> the type of exception that may be thrown
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Exception> {
        /**
         * Gets a result, potentially throwing an exception.
         *
         * @return a result
         * @throws E if unable to supply a result
         */
        T get() throws E;
    }

    /**
     * Monitors an API endpoint operation that may throw checked exceptions.
     *
     * @param operationName Name of the API action
     * @param operation     The operation to monitor
     * @param <T>           The return type
     * @param <E>           The exception type
     * @return Result of the operation
     * @throws E if the operation throws an exception
     */
    public static <T, E extends Exception> T monitorApiOperationWithException(
            String operationName, ThrowingSupplier<T, E> operation) throws E {
        long startTime = System.nanoTime();
        T result = null;
        Exception error = null;

        try {
            result = operation.get();
            return result;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logPerformance("API", operationName, durationMs, SLOW_API_THRESHOLD_MS, result, error);
        }
    }

    /**
     * Monitors an operation with a custom threshold.
     * Useful for operations that have specific performance requirements.
     *
     * @param category          Category of the operation (e.g., "DB", "API",
     *                          "CACHE")
     * @param operationName     Name of the operation
     * @param operation         The operation to monitor
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
        Exception error = null;

        try {
            result = operation.get();
            return result;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logPerformance(category, operationName, durationMs, threshold, result, error);
        }
    }

    private static void logPerformance(String category, String operationName,
            long durationMs, long threshold, Object result, Exception error) {
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
                    "[%s ERROR] %s failed after %dms%n"
                            + "  Category: %s%n"
                            + "  Operation: %s%n"
                            + "  Duration: %dms%n"
                            + "  Error Type: %s%n"
                            + "  Error Message: %s",
                    category, operationName, durationMs,
                    category, operationName, durationMs,
                    error.getClass().getSimpleName(),
                    error.getMessage());
            log.warning(errorLog, error);

        } else if (durationMs > threshold) {
            metrics.put("status", "SLOW");
            metrics.put("exceedsThresholdBy", durationMs - threshold);

            String slowLog = String.format(
                    "[SLOW %s] %s took %dms (threshold: %dms)%n"
                            + "  Exceeded by: %dms%n"
                            + "  Category: %s%n"
                            + "  Operation: %s",
                    category, operationName, durationMs, threshold,
                    durationMs - threshold,
                    category, operationName);
            log.warning(slowLog);

        } else {
            metrics.put("status", "OK");
            log.info(String.format("[%s] %s completed in %dms",
                    category, operationName, durationMs));
        }

        log.performance(operationName, durationMs, metrics);
    }
}
