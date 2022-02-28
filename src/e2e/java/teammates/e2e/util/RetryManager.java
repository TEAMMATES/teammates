package teammates.e2e.util;

import teammates.test.ThreadHelper;

/**
 * Handles running and retrying of {@link Retryable} tasks.
 * Generally, the methods retry tasks with exponential backoff until one of the following conditions is met:
 * <ul>
 *     <li>Task is successful (see specific method documentation for definition of success).</li>
 *     <li>Maximum retries are exceeded (as determined by the specified maximum delay).</li>
 *     <li>A {@link Throwable} of type specified in the task is encountered (this is thrown upwards).</li>
 * </ul>
 */
public final class RetryManager {

    private final int maxDelayInS;

    /**
     * Creates a new {@link RetryManager} that contains methods to retry tasks.
     *
     * @param maxDelayInS maximum delay (in seconds) to wait before final retry.
     */
    public RetryManager(int maxDelayInS) {
        this.maxDelayInS = maxDelayInS;
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until no exceptions of the specified
     * {@code recognizedExceptionTypes} are caught.
     *
     * @throws MaximumRetriesExceededException if maximum retries are exceeded.
     */
    @SafeVarargs
    @SuppressWarnings("PMD.AvoidCatchingThrowable") // allow users to catch specific errors e.g. AssertionError
    public final void runUntilNoRecognizedException(
            Retryable task, Class<? extends Throwable>... recognizedExceptionTypes)
            throws MaximumRetriesExceededException {
        for (int delay = 1; delay <= maxDelayInS; delay *= 2) {
            try {
                task.run();
                return;
            } catch (Throwable e) {
                if (!isThrowableTypeIn(e, recognizedExceptionTypes)) {
                    throw e;
                }
                // continue retry process
            }
            logFailure(task, delay);
            ThreadHelper.waitFor(delay * 1000);
        }
        try {
            task.run();
        } catch (Throwable e) {
            if (!isThrowableTypeIn(e, recognizedExceptionTypes)) {
                throw e;
            }
            throw new MaximumRetriesExceededException(task, e);
        }
    }

    @SafeVarargs
    private static boolean isThrowableTypeIn(Throwable e, Class<? extends Throwable>... recognizedExceptionTypes) {
        for (Class<? extends Throwable> recognizedExceptionType : recognizedExceptionTypes) {
            if (recognizedExceptionType.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    private static void logFailure(Retryable task, int delay) {
        System.out.println(task.getName() + " failed; waiting " + delay + "s before retry");
    }
}
