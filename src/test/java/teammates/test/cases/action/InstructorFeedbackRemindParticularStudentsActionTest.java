package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.ui.controller.InstructorFeedbackRemindParticularStudentsAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackRemindParticularStudentsAction}.
 */
public class InstructorFeedbackRemindParticularStudentsActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student5InCourse1");

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

        ______TS("Unsuccessful case: No user to remind, warning message generated");

        String[] paramsNoUserToRemind = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
        };

        InstructorFeedbackRemindParticularStudentsAction action = getAction(paramsNoUserToRemind);

        RedirectResult rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT));
        verifyNoTasksAdded(action);

        ______TS("Unsuccessful case: Feedback session not open, warning message generated");

        fs = typicalBundle.feedbackSessions.get("awaiting.session");
        String[] paramsFeedbackSessionNotOpen = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail()
        };

        action = getAction(paramsFeedbackSessionNotOpen);

        rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN));
        verifyNoTasksAdded(action);

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail()
        };

        action = getAction(paramsTypical);

        rr = getRedirectResult(action);
        assertTrue(rr.getStatusMessage().contains(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT));

        verifySpecifiedTasksAdded(action,
                TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME, 1);

    }

    @Override
    protected InstructorFeedbackRemindParticularStudentsAction getAction(String... params) {
        return (InstructorFeedbackRemindParticularStudentsAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student5InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_REMIND_USERLIST, studentNotSubmitFeedback.getEmail()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
}
