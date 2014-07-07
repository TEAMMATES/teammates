package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackSubmissionEditPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackSubmissionEditPageActionTest extends BaseActionTest {
    
    DataBundle dataBundle;
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("not enough parameters");
        
        String[] paramsWithoutCourseId = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithoutFeedbackSessionName = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId
        };
        
        verifyAssumptionFailure(paramsWithoutCourseId);
        verifyAssumptionFailure(paramsWithoutFeedbackSessionName);
        
        ______TS("typical success case");
        
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.USER_ID, instructor.googleId
        };
        
        InstructorFeedbackSubmissionEditPageAction a = getAction(params);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT
                + "?error=false"
                + "&" + Const.ParamsNames.USER_ID + "=" + instructor.googleId,
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        ______TS("masquerade mode");
        
        gaeSimulation.loginAsAdmin("admin.user");
        
        a = getAction(params);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT
                + "?error=false"
                + "&" + Const.ParamsNames.USER_ID + "=" + instructor.googleId,
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        ______TS("closed session case");
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
       
        session = dataBundle.feedbackSessions.get("closedSession");
        
        params = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.USER_ID, instructor.googleId
        };
        
        a = getAction(params);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT
                + "?"
                + "error=false"
                + "&" + Const.ParamsNames.USER_ID + "=" + instructor.googleId,
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, r.getStatusMessage());
        
    }
    
    private InstructorFeedbackSubmissionEditPageAction getAction(String... params) throws Exception{
        return (InstructorFeedbackSubmissionEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
