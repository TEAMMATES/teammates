package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");
        String courseId1 = course1.getId();
        FeedbackSessionAttributes fsa1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes fsa2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student2 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student3 = typicalBundle.students.get("student1InCourse3");

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId1);
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        verifyHttpParameterFailure(paramsInvalid);

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulAccess));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: typical submission");
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa2.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student2.getEmail(),
        };
        response = getJsonResult(getAction(paramsSuccessfulSubmission));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: should create even for invalid parameters");
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        response = getJsonResult(getAction(paramsNonExistentFsName));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@email.com",
        };
        response = getJsonResult(getAction(paramsNonExistentStudentEmail));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: should create even when student cannot access feedback session in course");
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student3.getEmail(),
        };
        response = getJsonResult(getAction(paramsWithoutAccess));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }
}
