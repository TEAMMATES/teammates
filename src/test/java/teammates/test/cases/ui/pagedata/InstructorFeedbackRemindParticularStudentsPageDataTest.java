package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseComponentTestCase;

public class InstructorFeedbackRemindParticularStudentsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
    }
}