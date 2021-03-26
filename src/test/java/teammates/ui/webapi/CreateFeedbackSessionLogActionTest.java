package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.TRACK_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        JsonResult actionOutput;

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
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );

        ______TS("Failure case: invalid log type");
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsInvalid));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Success case: typical access");
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.ACCESS,
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessfulAccess));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        ______TS("Success case: typical submission");
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa2.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, student2.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessfulSubmission));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        ______TS("Success case: should create even for invalid parameters");
        String[] paramsNonExistentCourseId = {
                Const.ParamsNames.COURSE_ID, "non-existent-course-id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsNonExistentCourseId));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsNonExistentFsName));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@email.com",
        };
        actionOutput = getJsonResult(getAction(paramsNonExistentStudentEmail));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        ______TS("Success case: should create even when student cannot access feedback session in course");
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, Const.FeedbackSessionLogTypes.SUBMISSION,
                Const.ParamsNames.STUDENT_EMAIL, student3.getEmail(),
        };
        actionOutput = getJsonResult(getAction(paramsWithoutAccess));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
