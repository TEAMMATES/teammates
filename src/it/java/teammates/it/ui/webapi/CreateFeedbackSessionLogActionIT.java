package teammates.it.ui.webapi;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.CreateFeedbackSessionLogAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionIT extends BaseActionIT<CreateFeedbackSessionLogAction> {

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
    protected void testExecute() throws Exception {
        Course course1 = typicalBundle.courses.get("course1");
        String courseId1 = course1.getId();
        FeedbackSession fs1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession fs2 = typicalBundle.feedbackSessions.get("session2InTypicalCourse");
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student2 = typicalBundle.students.get("student2InCourse1");
        Student student3 = typicalBundle.students.get("student1InCourse3");

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId1);
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_SQL_ID, student2.getId().toString()
        );

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        verifyHttpParameterFailure(paramsInvalid);

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulAccess));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: typical submission");
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs2.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student2.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs2.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student2.getId().toString(),
        };
        response = getJsonResult(getAction(paramsSuccessfulSubmission));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: should create even for invalid parameters");
        String[] paramsNonExistentCourseId = {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        response = getJsonResult(getAction(paramsNonExistentCourseId));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: should create even for invalid parameters");
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, UUID.randomUUID().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        response = getJsonResult(getAction(paramsNonExistentFsName));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@email.com",
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, UUID.randomUUID().toString(),
        };
        response = getJsonResult(getAction(paramsNonExistentStudentEmail));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());

        ______TS("Success case: should create even when student cannot access feedback session in course");
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student3.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.STUDENT_SQL_ID, student3.getId().toString(),
        };
        response = getJsonResult(getAction(paramsWithoutAccess));
        output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
