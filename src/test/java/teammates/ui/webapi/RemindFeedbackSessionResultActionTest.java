package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;

/**
 * SUT: {@link RemindFeedbackSessionResultAction}.
 */
public class RemindFeedbackSessionResultActionTest extends BaseActionTest<RemindFeedbackSessionResultAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes studentToEmail = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructorToEmail = typicalBundle.instructors.get("instructor2OfCourse1");
        String[] usersToRemind = new String[2];
        usersToRemind[0] = studentToEmail.getEmail();
        usersToRemind[1] = instructorToEmail.getEmail();

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

        ______TS("Unsuccessful case: Feedback session not published, warning message generated");

        fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsFeedbackSessionNotPublished = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        FeedbackSessionRespondentRemindRequest remindRequest = new FeedbackSessionRespondentRemindRequest();
        remindRequest.setUsersToRemind(usersToRemind);

        InvalidOperationException ioe = verifyInvalidOperation(remindRequest, paramsFeedbackSessionNotPublished);
        assertEquals("Published email could not be resent "
                + "as the feedback session is not published.", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("closedSession");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        RemindFeedbackSessionResultAction validAction = getAction(remindRequest, paramsTypical);
        getJsonResult(validAction);

        verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
