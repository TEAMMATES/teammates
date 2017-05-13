package teammates.common.util;

import teammates.test.driver.TestProperties;

public class RetryManager {
    public static <ResultType> ResultType runWithRetry(RetryableWithResult<ResultType> task) {
        runWithRetry((Retryable) task);
        return task.getResult();
    }

    public static void runWithRetry(Retryable task) {
        boolean isSuccessful = task.run();
        for (int delay = 1; !isSuccessful && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            System.out.println("Failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            task.beforeRetry();
            isSuccessful = task.run();
        }
    }
}
