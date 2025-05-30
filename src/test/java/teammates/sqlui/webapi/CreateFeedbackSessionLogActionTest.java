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
    Course course1;
    Course course2;
    Course courseNoStudent;
    String courseId1;

    FeedbackSession fsaCourse1;
    FeedbackSession fsaCourseNoStudent;
    String fsaCourse1Id;
    String fsaCourse1Name;
    String fsaCourseNoStudentId;
    String fsaCourseNoStudentName;

    Student student1InCourse1;
    Student student2InCourse2;
    Student student3InCourse2;
    String student1Email;
    String student2Email;
    String student3Email;
    String student1Id;
    String student2Id;
    String student3Id;

    String accessLabel;
    String submissionLabel;

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
        courseNoStudent = getTypicalCourse();
        course2.setId("course2");
        courseId1 = course1.getId();
        courseNoStudent.setId("courseNoStudent");

        fsaCourse1 = getTypicalFeedbackSessionForCourse(course1);
        fsaCourseNoStudent = getTypicalFeedbackSessionForCourse(courseNoStudent);
        fsaCourse1Id = fsaCourse1.getId().toString();
        fsaCourseNoStudentId = fsaCourseNoStudent.getId().toString();
        fsaCourse1Name = fsaCourse1.getName();
        fsaCourseNoStudentName = fsaCourseNoStudent.getName();

        student1InCourse1 = getTypicalStudent();
        student2InCourse2 = getTypicalStudent();
        student3InCourse2 = getTypicalStudent();
        student1InCourse1.setCourse(course1);
        student2InCourse2.setCourse(course1);
        student3InCourse2.setCourse(course2);
        student1Email = student1InCourse1.getEmail();
        student2Email = student2InCourse2.getEmail();
        student3Email = student3InCourse2.getEmail();
        student1Id = student1InCourse1.getId().toString();
        student2Id = student2InCourse2.getId().toString();
        student3Id = student3InCourse2.getId().toString();

        accessLabel = FeedbackSessionLogType.ACCESS.getLabel();
        submissionLabel = FeedbackSessionLogType.SUBMISSION.getLabel();
    }

    @Test
    void testAccessControl() {
        verifyAnyUserCanAccess();
    }

    @Test
    void testExecute_typicalAccess_shouldSucceed() {
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, accessLabel,
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.STUDENT_SQL_ID, student1Id,
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourseNoStudentName,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourseNoStudentId,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, submissionLabel,
                Const.ParamsNames.STUDENT_EMAIL, student2Email,
                Const.ParamsNames.STUDENT_SQL_ID, student2Id,

        };
        JsonResult response = getJsonResult(getAction(paramsSuccessfulSubmission));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_invalidSessionName_shouldStillSucceed() {
        String nonExistentSessionName = "non-existent-feedback-session-name";
        String[] paramsNonExistentFsName = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonExistentSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, submissionLabel,
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.STUDENT_SQL_ID, student1Id,
        };
        JsonResult response = getJsonResult(getAction(paramsNonExistentFsName));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_invalidEmail_shouldStillSucceed() {
        String nonExistentEmail = "non-existent-student@email.com";
        String[] paramsNonExistentStudentEmail = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, submissionLabel,
                Const.ParamsNames.STUDENT_EMAIL, nonExistentEmail,
                Const.ParamsNames.STUDENT_SQL_ID, student1Id,
        };
        JsonResult response = getJsonResult(getAction(paramsNonExistentStudentEmail));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_studentHasNoAccessToCourseFeedback_shouldStillSucceed() {
        String[] paramsWithoutAccess = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, submissionLabel,
                Const.ParamsNames.STUDENT_EMAIL, student3Email,
                Const.ParamsNames.STUDENT_SQL_ID, student3Id,
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, submissionLabel,
                Const.ParamsNames.STUDENT_EMAIL, student1Email
        );
    }

    @Test
    void testExecute_invalidLogType_shouldFail() {
        String invalidLogType = "invalid log type";
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsaCourse1Name,
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, invalidLogType,
                Const.ParamsNames.STUDENT_EMAIL, student1Email,
                Const.ParamsNames.STUDENT_SQL_ID, student1Id,
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

}
