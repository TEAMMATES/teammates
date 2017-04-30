package teammates.test.cases.search;

import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseComponentTestCase;

/**
 * Base class for all search tests.
 */
public abstract class BaseSearchTest extends BaseComponentTestCase {

    protected DataBundle dataBundle;

    @BeforeClass
    public void baseClassSetup() {
        prepareTestData();
    }

    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
        putDocuments(dataBundle);
    }

}
