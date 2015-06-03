package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.datatransfer.DataBundle;
import teammates.ui.controller.InstructorFeedbackQuestionVisibilityMessageAction;
import teammates.ui.controller.InstructorFeedbackQuestionVisibilityMessagePageData;
import teammates.ui.controller.ActionResult;

public class InstructorFeedbackQuestionVisibilityMessageActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_HOME_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        
    }

    private InstructorFeedbackQuestionVisibilityMessageAction getAction(String... params) throws Exception {
        return (InstructorFeedbackQuestionVisibilityMessageAction) (gaeSimulation.getActionObject(uri, params));
    }
}