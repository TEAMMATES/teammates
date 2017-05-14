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
    public static <T, E extends Throwable> T runWithRetry(RetryableWithResult<T, E> task) throws E {
        runWithRetry((Retryable<E>) task);
        return task.getResult();
    }

    /**
     * Runs {@code task} and retries if needed.
     */
    public static <E extends Throwable> void runWithRetry(Retryable<E> task) throws E {
        boolean isSuccessful = task.run();
        for (int delay = 1; !isSuccessful && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            System.out.println(task.getName() + " failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            isSuccessful = task.run();
        }
    }
}
