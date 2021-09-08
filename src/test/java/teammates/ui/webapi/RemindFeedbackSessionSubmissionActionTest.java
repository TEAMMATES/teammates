package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;

/**
 * SUT: {@link RemindFeedbackSessionSubmissionAction}.
 */
public class RemindFeedbackSessionSubmissionActionTest extends BaseActionTest<RemindFeedbackSessionSubmissionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_SUBMISSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student5InCourse1");
        InstructorAttributes instructorNotSubmitFeedback = typicalBundle.instructors.get("instructor2OfCourse1");
        String[] usersToRemind = new String[2];
        usersToRemind[0] = studentNotSubmitFeedback.getEmail();
        usersToRemind[1] = instructorNotSubmitFeedback.getEmail();

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Unsuccessful case: Not enough parameters");
        verifyHttpParameterFailure();
        String[] paramsNoCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };
        verifyHttpParameterFailure(paramsNoCourseId);
        String[] paramsNoFeedback = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
        };
        verifyHttpParameterFailure(paramsNoFeedback);

        ______TS("Unsuccessful case: Feedback session not open, warning message generated");

        fs = typicalBundle.feedbackSessions.get("awaiting.session");
        String[] paramsFeedbackSessionNotOpen = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        InvalidOperationException ioe = verifyInvalidOperation(remindRequest, paramsFeedbackSessionNotOpen);
        assertEquals("Reminder email could not be sent out "
                + "as the feedback session is not open for submissions.", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        RemindFeedbackSessionSubmissionAction validAction = getAction(remindRequest, paramsTypical);
        getJsonResult(validAction);

        verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
