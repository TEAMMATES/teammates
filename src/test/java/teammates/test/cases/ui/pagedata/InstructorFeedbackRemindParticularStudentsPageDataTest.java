package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseComponentTestCase;

public class InstructorFeedbackRemindParticularStudentsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
    }

    @Test
    public void testInitWithoutDefaultFormValues() throws Exception {
        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
    }
}
