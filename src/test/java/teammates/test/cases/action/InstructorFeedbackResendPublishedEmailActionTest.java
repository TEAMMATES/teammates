package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.ui.controller.InstructorFeedbackResendPublishedEmailAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackResendPublishedEmailAction}.
 */
public class InstructorFeedbackResendPublishedEmailActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESEND_PUBLISHED_EMAIL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes studentToEmail = typicalBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Unsuccessful case: Not enough parameters");
        String[] paramsNoCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };
        verifyAssumptionFailure(paramsNoCourseId);
        String[] paramsNoFeedback = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId()
        };
        verifyAssumptionFailure(paramsNoFeedback);

        ______TS("Unsuccessful case: No user to email, warning message generated");

        String[] paramsNoUserToEmail = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };

        InstructorFeedbackResendPublishedEmailAction action = getAction(paramsNoUserToEmail);

        RedirectResult rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT));
        verifyNoTasksAdded(action);

        ______TS("Unsuccessful case: Feedback session not published, warning message generated");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsFeedbackSessionNotPublshed = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
                Const.ParamsNames.SUBMISSION_RESEND_PUBLISHED_EMAIL_USER_LIST, studentToEmail.getEmail()
        };

        action = getAction(paramsFeedbackSessionNotPublshed);

        rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_NOT_PUBLISHED));
        verifyNoTasksAdded(action);

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("closedSession");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
                Const.ParamsNames.SUBMISSION_RESEND_PUBLISHED_EMAIL_USER_LIST, studentToEmail.getEmail()
        };

        action = getAction(paramsTypical);

        rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_RESEND_EMAIL_SENT));

        verifySpecifiedTasksAdded(action,
                TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME, 1);

    }

    @Override
    protected InstructorFeedbackResendPublishedEmailAction getAction(String... params) {
        return (InstructorFeedbackResendPublishedEmailAction)
                gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_RESEND_PUBLISHED_EMAIL_USER_LIST, studentNotSubmitFeedback.getEmail()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
}
