package teammates.test.driver;

import teammates.common.util.ThreadHelper;

/**
 * Handles running and retrying of {@code Retryable} tasks.
 */
public final class RetryManager {

    private RetryManager() {
        // utility class
    }

    /**
     * Runs {@code task} and retries if needed.
     * Returns {@code task} result.
     */
    public static <T> T runWithRetry(RetryableWithResult<T> task) {
        runWithRetry((Retryable) task);
        return task.getResult();
    }

    /**
     * Runs {@code task} and retries if needed.
     */
    public static void runWithRetry(Retryable task) {
        boolean isSuccessful = task.run();
        for (int delay = 1; !isSuccessful && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            System.out.println(task + " failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            isSuccessful = task.run();
        }
    }
}
