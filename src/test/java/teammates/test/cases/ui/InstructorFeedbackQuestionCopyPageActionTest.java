package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackQuestionCopyPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackQuestionCopyPageActionTest extends BaseActionTest {
    
    private final DataBundle dataBundle = getTypicalDataBundle();
        
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
        ______TS("typical success case");
        
        FeedbackSessionAttributes feedbackSessionAttributes =
                dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName()
        };
        
        InstructorFeedbackQuestionCopyPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();
        
        String expectedString = Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_MODAL
                         + "?error=false&user=" + instructor1OfCourse1.googleId;
        assertEquals(expectedString, result.getDestinationWithParams());
        
        assertTrue(result.getStatusMessage().isEmpty());
        
        ______TS("failure: non-existent feedback session");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Non-existent Session Name"
        };
        
        action = getAction(submissionParams);
        try {
            result = (ShowPageResult) action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         uae.getMessage());
        }
        
        ______TS("failure: unsufficient permissions");
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("helperOfCourse1").googleId);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName()
        };
        
        action = getAction(submissionParams);
        try {
            result = (ShowPageResult) action.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Feedback session [First feedback session] is not accessible "
                         + "to instructor [helper@course1.tmt] for privilege [canmodifysession]",
                         uae.getMessage());
        }
    }
    
    private InstructorFeedbackQuestionCopyPageAction getAction(String... params) {
        return (InstructorFeedbackQuestionCopyPageAction) gaeSimulation.getActionObject(uri, params);
    }
}
