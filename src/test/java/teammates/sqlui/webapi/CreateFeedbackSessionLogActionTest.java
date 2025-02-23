package teammates.sqlui.webapi;

import org.testng.annotations.BeforeSuite;
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
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    private static final String GOOGLE_ID = "user-googleId";

    Course course1;
    Course course2;
    Course course3;
    String courseId1;
    FeedbackSession fsa1;
    FeedbackSession fsa2;
    Student student1;
    Student student2;
    Student student3;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeSuite
    protected void prepareData() {
        course1 = getTypicalCourse();
        course2 = getTypicalCourse();
        course3 = getTypicalCourse();
        course2.setId("course2");
        course3.setId("course3");
        courseId1 = course1.getId();
        fsa1 = getTypicalFeedbackSessionForCourse(course1);
        fsa2 = getTypicalFeedbackSessionForCourse(course2);
        student1 = getTypicalStudent();
        student2 = getTypicalStudent();
        student3 = getTypicalStudent();
        student1.setCourse(course1);
        student2.setCourse(course1);
        student3.setCourse(course3);
    }

    @Test
    void testAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainers_canAccess() {
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_instructor_canAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_student_canAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_loggedOut_canAccess() {
        logoutUser();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_unregistered_canAccess() {
        loginAsUnregistered(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testExecute_typicalAccess_shouldSucceed() {
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        CreateFeedbackSessionLogAction action = getAction(paramsSuccessfulAccess);
        JsonResult response = getJsonResult(action);
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_typicalSubmission_shouldSucceed() {
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa2.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa2.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student2.getEmail(),
                Const.ParamsNames.STUDENT_SQL_ID, student2.getId().toString(),

        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulSubmission));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_invalidSessionName_shouldStillSucceed() {
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non-existent-feedback-session-name",
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsNonExistentFsName));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_invalidEmail_shouldStillSucceed() {
        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@email.com",
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsNonExistentStudentEmail));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_studentHasNoAccessToCourseFeedback_shouldStillSucceed() {
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student3.getEmail(),
                Const.ParamsNames.STUDENT_SQL_ID, student3.getId().toString(),
        };
        JsonResult response = getJsonResult(getAction(paramsWithoutAccess));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_notEnoughParameters_shouldFail() {
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId1);
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
    }

    @Test
    void testExecute_invalidLogType_shouldFail() {
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

}
