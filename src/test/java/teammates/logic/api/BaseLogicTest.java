package teammates.logic.api;

import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * Base class for all *Logic tests.
 */
public abstract class BaseLogicTest extends BaseTestCaseWithLocalDatabaseAccess {

    DataBundle dataBundle;

    @BeforeClass
    public void baseClassSetup() {
        prepareTestData();
    }

    void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

}
