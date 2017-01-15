package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

public class InstructorFeedbackRemindActionTest extends BaseActionTest {
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        // This test case was omitted as the action is executed in the background task queue
        // so we cannot determine its result.
          
        // The logic under the action is covered with test case to ensure the action works.
    }
}
