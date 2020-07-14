package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.RemindFeedbackSessionResultAction;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackSessionStudentRemindRequest;

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
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes studentToEmail = typicalBundle.students.get("student1InCourse1");

        loginAsInstructor(instructor1ofCourse1.googleId);

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
        String[] paramsFeedbackSessionNotPublshed = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        FeedbackSessionStudentRemindRequest remindRequest = new FeedbackSessionStudentRemindRequest();
        remindRequest.setUsersToRemind(studentToEmail.getEmail().split(""));

        RemindFeedbackSessionResultAction action = getAction(remindRequest, paramsFeedbackSessionNotPublshed);
        JsonResult result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals("Published email could not be resent "
                + "as the feedback session is not published.", message.getMessage());
        verifyNoTasksAdded(action);

        ______TS("Successful case: Typical case");

        fs = typicalBundle.feedbackSessions.get("closedSession");
        String[] paramsTypical = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        action = getAction(remindRequest, paramsTypical);
        result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifySpecifiedTasksAdded(action,
                Const.TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("closedSession");
        StudentAttributes studentNotSubmitFeedback = typicalBundle.students.get("student1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.SUBMISSION_RESEND_PUBLISHED_EMAIL_USER_LIST, studentNotSubmitFeedback.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, submissionParams);
    }

}
