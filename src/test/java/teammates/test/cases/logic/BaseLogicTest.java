package teammates.test.cases.logic;

import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseComponentTestCase;

/**
 * Base class for all *Logic tests.
 */
public abstract class BaseLogicTest extends BaseComponentTestCase {

    protected DataBundle dataBundle;

    @BeforeClass
    public void baseClassSetup() {
        prepareTestData();
    }

    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

}
