package teammates.test.driver.retry;

import teammates.common.util.Assumption;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.TestProperties;

/**
 * Handles running and retrying of {@code Retryable} tasks.
 */
public final class RetryManager {

    private static final int MAX_DELAY_IN_S = TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2;

    private RetryManager() {
        // utility class
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
     * Returns {@code task} result or null if none.
     */
    public static <T, E extends Throwable> T runUntilSuccessful(Retryable<T, E> task) throws E {
        return doRetry(task, SuccessCondition.DEFAULT);
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until task returns a non-null result.
     * Returns {@code task} result or null if none.
     */
    public static <T, E extends Throwable> T runUntilNotNull(RetryableTaskReturnsThrows<T, E> task) throws E {
        return doRetry(task, SuccessCondition.NOT_NULL);
    }

    /**
     * Runs {@code task}, retrying if needed using exponential backoff, until no exceptions of class
     * {@code exceptionType} are caught.
     * Returns {@code task} result or null if none.
     */
    public static <T, E extends Throwable, C extends Throwable> T runUntilNoException(
            Retryable<T, E> task, Class<C> exceptionType) throws E {
        return doRetry(task, exceptionType);
    }

    private static <T, E extends Throwable> T doRetry(Retryable<T, E> task, SuccessCondition condition)
            throws E {
        T result = task.runExec();
        for (int delay = 1; !condition.isSuccessful(task) && delay <= MAX_DELAY_IN_S; delay *= 2) {
            logFailure(task, delay);
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            result = task.runExec();
        }
        return result;
    }

    @SuppressWarnings("PMD.AvoidCatchingThrowable") // allow users to catch specific errors e.g. AssertionError
    private static <T, E extends Throwable, C extends Throwable> T doRetry(
            Retryable<T, E> task, Class<C> exceptionType) throws E {
        for (int delay = 1; delay <= MAX_DELAY_IN_S; delay *= 2) {
            try {
                return task.runExec();
            } catch (Throwable e) {
                if (!exceptionType.isInstance(e)) {
                    throw e;
                }
                // continue retry process
            }
            logFailure(task, delay);
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
        }
        return task.runExec();
    }

    private static <T, E extends Throwable> void logFailure(Retryable<T, E> task, int delay) {
        System.out.println(task.getName() + " failed; waiting " + delay + "s before retry");
    }
}
