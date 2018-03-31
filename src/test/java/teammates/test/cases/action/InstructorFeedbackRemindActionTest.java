package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.ui.controller.InstructorFeedbackRemindAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackRemindAction}.
 */
public class InstructorFeedbackRemindActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Unsuccessful case: Not enough parameters");
        verifyAssumptionFailure();
        String[] paramsNoCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };
        verifyAssumptionFailure(paramsNoCourseId);
        String[] paramsNoFeedback = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId()
        };
        verifyAssumptionFailure(paramsNoFeedback);

        ______TS("Unsuccessful case: Feedback session not open, warning message generated");

        fs = typicalBundle.feedbackSessions.get("awaiting.session");
        String[] paramsFeedbackSessionNotOpen = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName()
        };

        InstructorFeedbackRemindAction action = getAction(paramsFeedbackSessionNotOpen);

        RedirectResult rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN));
        verifyNoTasksAdded(action);

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };

        action = getAction(paramsTypical);

        rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT));

        verifySpecifiedTasksAdded(action,
                TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_QUEUE_NAME, 1);

    }

    @Override
    protected InstructorFeedbackRemindAction getAction(String... params) {
        return (InstructorFeedbackRemindAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
