package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.ui.controller.InstructorFeedbackRemindAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackRemindActionTest extends BaseActionTest {
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorFeedbackRemindAction action;
        RedirectResult rr;
        
        ______TS("Unsuccessful case: Not owned Course/Modify Permission, authentication failure");
        String[] paramsNotOwnedCourse = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };
        verifyUnaccessibleForInstructorsOfOtherCourses(paramsNotOwnedCourse);
        verifyUnaccessibleWithoutModifySessionPrivilege(paramsNotOwnedCourse);
        
        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        
        ______TS("Unsuccessful case: Not enough Parameter");
        verifyAssumptionFailure();
        String[] paramsNoCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };
        verifyAssumptionFailure(paramsNoCourseId);
        String[] paramsNoFeedback = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId()
        };
        verifyAssumptionFailure(paramsNoFeedback);
        
        ______TS("Successful case: Typical case");
        
        String[] paramsTypical = new String[]{
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };
        
        action = getAction(paramsTypical);
        
        rr = (RedirectResult) action.executeAndPostProcess();
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT));
        
        verifySpecifiedTasksAdded(action,
                TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_QUEUE_NAME, 1);
        
    }
    
    private InstructorFeedbackRemindAction getAction(String... params) {
        return (InstructorFeedbackRemindAction) gaeSimulation.getActionObject(uri, params);
    }
}
