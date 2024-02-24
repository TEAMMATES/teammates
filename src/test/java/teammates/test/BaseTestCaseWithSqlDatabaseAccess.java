package teammates.test;

import teammates.common.datatransfer.SqlDataBundle;

/**
 * Base class for all test cases which are allowed to access the database.
 */
public abstract class BaseTestCaseWithSqlDatabaseAccess extends BaseTestCase {

    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    /**
     * Removes and restores the databundle, with retries.
     */
    protected void removeAndRestoreDataBundle(SqlDataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        boolean isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        while (!isOperationSuccess && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle");
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        }
        assertTrue(isOperationSuccess);
    }

    protected abstract boolean doRemoveAndRestoreDataBundle(SqlDataBundle testData);

}
