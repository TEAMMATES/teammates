package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResultsByRGQSeeMorePageAction;
import teammates.ui.controller.InstructorFeedbackResultsByRGQSeeMorePageData;

public class InstructorFeedbackResultsByRGQSeeMorePageActionTest extends
        BaseActionTest {
    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_AJAX_BY_RGQ;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testAccessControl() throws Exception {
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.SECTION_NAME, "Section A"
        };
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
    
    @Test
    public void testExcecuteAndPostProcess() throws Exception {
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        gaeSimulation.loginAsInstructor(instructor1.googleId);
        
        ______TS("Unsuccessful case: not enough parameters");
        
        verifyAssumptionFailure();
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyAssumptionFailure(submissionParams);
        
        ______TS("typical successful case");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.SECTION_NAME, "Section 1"
        };
        
        InstructorFeedbackResultsByRGQSeeMorePageAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();
        InstructorFeedbackResultsByRGQSeeMorePageData data = 
                (InstructorFeedbackResultsByRGQSeeMorePageData) result.data;
        assertEquals(data.answer.values().size(), 5);
    }
    
    private InstructorFeedbackResultsByRGQSeeMorePageAction getAction(String... params) throws Exception {
        return (InstructorFeedbackResultsByRGQSeeMorePageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
