package teammates.common.util.retry;

import teammates.common.util.Assumption;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;

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

    private static final Logger log = Logger.getLogger();

    private final int maxDelayInS;

    /**
     * Creates a new {@link RetryManager} that contains methods to retry tasks.
     *
     * @param maxDelayInS maximum delay (in seconds) to wait before final retry.
     */
    public RetryManager(int maxDelayInS) {
        this.maxDelayInS = maxDelayInS;
    }

    private enum SuccessCondition {
        /**
         * The task's {@code isSuccessfulExec()} method must return true for the task to be considered successful.
         */
        DEFAULT {
            @Override
            public <T, E extends Throwable> boolean isSuccessful(Retryable<T, E> task) throws E {
                return task.isSuccessfulExec();
            }
        },

        /**
         * The task's {@code isResultNull()} method must return false for the task to be considered successful.
         * Only applicable if the task is an instance of {@link RetryableTaskReturnsThrows}.
         */
        NOT_NULL {
            @Override
            public <T, E extends Throwable> boolean isSuccessful(Retryable<T, E> task) throws E {
                Assumption.assertTrue("Success condition " + NOT_NULL + " is only applicable to subclasses of "
                        + RetryableTaskReturnsThrows.class.getSimpleName(),
                        RetryableTaskReturnsThrows.class.isInstance(task));

                return !((RetryableTaskReturnsThrows<T, E>) task).isResultNull();
            }
        };

        /**
         * Checks whether the {@code task} ran successfully based based on the {@code SuccessCondition}.
         */
        public abstract <T, E extends Throwable> boolean isSuccessful(Retryable<T, E> task) throws E;
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until task is successful.
     *
     * @returns {@code task} result or null if none.
     * @throws E if encountered while running or evaluating {@code task}.
     * @throws MaximumRetriesExceededException if maximum retries are exceeded.
     */
    public <T, E extends Throwable> T runUntilSuccessful(Retryable<T, E> task) throws E, MaximumRetriesExceededException {
        return doRetry(task, SuccessCondition.DEFAULT);
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until task returns a non-null result.
     *
     * @returns {@code task} result or null if none.
     * @throws E if encountered while running or evaluating {@code task}.
     * @throws MaximumRetriesExceededException if maximum retries are exceeded.
     */
    public <T, E extends Throwable> T runUntilNotNull(RetryableTaskReturnsThrows<T, E> task)
            throws E, MaximumRetriesExceededException {
        return doRetry(task, SuccessCondition.NOT_NULL);
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until no exceptions of the specified
     * {@code recognizedExceptionTypes} are caught.
     *
     * @returns {@code task} result or null if none.
     * @throws E if encountered while running or evaluating {@code task}.
     * @throws MaximumRetriesExceededException if maximum retries are exceeded.
     */
    @SafeVarargs
    public final <T, E extends Throwable> T runUntilNoRecognizedException(
            Retryable<T, E> task, Class<? extends Throwable>... recognizedExceptionTypes)
            throws E, MaximumRetriesExceededException {
        return doRetry(task, recognizedExceptionTypes);
    }

    private <T, E extends Throwable> T doRetry(Retryable<T, E> task, SuccessCondition condition)
            throws E, MaximumRetriesExceededException {
        T result = task.runExec();
        for (int delay = 1; !condition.isSuccessful(task) && delay <= maxDelayInS; delay *= 2) {
            logFailure(task, delay);
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            result = task.runExec();
        }
        if (!condition.isSuccessful(task)) {
            throw new MaximumRetriesExceededException(task);
        }
        return result;
    }

    @SafeVarargs
    @SuppressWarnings({
            "PMD.AvoidCatchingThrowable", // allow users to catch specific errors e.g. AssertionError
            "PMD.UnnecessaryFinalModifier" // necessary for @SafeVarargs annotation
    })
    private final <T, E extends Throwable> T doRetry(
            Retryable<T, E> task, Class<? extends Throwable>... recognizedExceptionTypes)
            throws E, MaximumRetriesExceededException {
        for (int delay = 1; delay <= maxDelayInS; delay *= 2) {
            try {
                return task.runExec();
            } catch (Throwable e) {
                if (!isThrowableTypeIn(e, recognizedExceptionTypes)) {
                    throw e;
                }
                // continue retry process
            }
            logFailure(task, delay);
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
        }
        try {
            return task.runExec();
        } catch (Throwable e) {
            if (!isThrowableTypeIn(e, recognizedExceptionTypes)) {
                throw e;
            }
            throw new MaximumRetriesExceededException(task, e);
        }
    }

    @SafeVarargs
    private static boolean isThrowableTypeIn(Throwable e, Class<? extends Throwable>... recognizedExceptionTypes) {
        for (Class recognizedExceptionType : recognizedExceptionTypes) {
            if (recognizedExceptionType.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    private static <T, E extends Throwable> void logFailure(Retryable<T, E> task, int delay) {
        log.info(task.getName() + " failed; waiting " + delay + "s before retry");
    }
}
