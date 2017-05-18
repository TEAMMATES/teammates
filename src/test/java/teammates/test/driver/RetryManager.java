package teammates.test.driver;

import teammates.common.util.ThreadHelper;

/**
 * Handles running and retrying of {@code Retryable} tasks.
 */
public final class RetryManager {

    private static final int MAX_DELAY_IN_S = TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2;

    private RetryManager() {
        // utility class
    }

    /**
     * Runs {@code task} and retries if needed using exponential backoff.
     * Returns {@code task} result.
     */
    public static <T, E extends Throwable> T runWithRetry(RetryableReturns<T, E> task) throws E {
        runWithRetry((Retryable<E>) task);
        return task.getResult();
    }

    /**
     * Runs {@code task} and retries if needed using exponential backoff.
     */
    public static <E extends Throwable> void runWithRetry(Retryable<E> task) throws E {
        boolean isSuccessful = task.run();
        for (int delay = 1; !isSuccessful && delay <= MAX_DELAY_IN_S; delay *= 2) {
            System.out.println(task.getName() + " failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            isSuccessful = task.run();
        }
    }
}
