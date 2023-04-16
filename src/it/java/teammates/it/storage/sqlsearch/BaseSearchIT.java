package teammates.it.storage.sqlsearch;

import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;

/**
 * Base class for all search tests.
 */
public abstract class BaseSearchIT extends BaseTestCaseWithSqlDatabaseAccess {

    SqlDataBundle typicalBundle = getTypicalSqlDataBundle();

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        putDocuments(typicalBundle);
        HibernateUtil.flushSession();
    }
}
