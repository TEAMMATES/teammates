package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.CreateFeedbackSessionLogAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    private static final String MATCHING_STUDENT_USER_ID = "matching.student";
    private static final String NON_MATCHING_STUDENT_USER_ID = "nonmatching.student";

    Course course1;
    Course course2;
    Course courseNoStudent;
    String courseId1;

    FeedbackSession fsaCourse1;
    FeedbackSession fsaCourseNoStudent;
    String fsaCourse1Id;

    Student student1InCourse1;
    Student student2InCourse2;
    Student student3InCourse2;
    String student1RegKey;

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

        student1InCourse1 = getTypicalStudent();
        student2InCourse2 = getTypicalStudent();
        student3InCourse2 = getTypicalStudent();
        student1InCourse1.setCourse(course1);
        student2InCourse2.setCourse(courseNoStudent);
        student3InCourse2.setCourse(course2);
        student1RegKey = student1InCourse1.getRegKey();
    }

    @BeforeMethod
    void setUp() {
        reset(mockLogic);
        when(mockLogic.getStudent(student1InCourse1.getId())).thenReturn(student1InCourse1);
        when(mockLogic.getStudent(student2InCourse2.getId())).thenReturn(student2InCourse2);
        when(mockLogic.getStudent(student3InCourse2.getId())).thenReturn(student3InCourse2);
        when(mockLogic.getStudentByRegistrationKey(student1RegKey)).thenReturn(student1InCourse1);
        when(mockLogic.getStudentByGoogleId(courseId1, MATCHING_STUDENT_USER_ID)).thenReturn(student1InCourse1);
        when(mockLogic.getStudentByGoogleId(courseId1, NON_MATCHING_STUDENT_USER_ID))
                .thenReturn(student2InCourse2);
        when(mockLogic.getFeedbackSession(fsaCourse1.getId())).thenReturn(fsaCourse1);
        when(mockLogic.getFeedbackSession(fsaCourseNoStudent.getId())).thenReturn(fsaCourseNoStudent);
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
        };

        loginAsStudent(MATCHING_STUDENT_USER_ID);
        verifyCanAccess(params);

        loginAsStudent(NON_MATCHING_STUDENT_USER_ID);
        verifyCannotAccess(params);

        loginAsAdmin();
        verifyCannotAccess(params);

        loginAsInstructor("instructor.user");
        verifyCannotAccess(params);

        loginAsUnregistered("unregistered.user");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);

        String[] paramsWithRegKey = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
                Const.ParamsNames.REGKEY, student1RegKey,
        };
        verifyCanAccess(paramsWithRegKey);
    }

    @Test
    void testExecute_typicalAccess_shouldSucceed() throws Exception {
        loginAsStudent(MATCHING_STUDENT_USER_ID);
        String[] paramsSuccessfulAccess = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.ACCESS.name(),
        };

        CreateFeedbackSessionLogAction action = getAction(paramsSuccessfulAccess);
        JsonResult response = getJsonResult(action);
        MessageOutput output = (MessageOutput) response.getOutput();

        assertEquals("Successful", output.getMessage());
        verify(mockLogic).createFeedbackSessionLog(eq(fsaCourse1), eq(student1InCourse1),
                eq(FeedbackSessionLogType.ACCESS), any());
    }

    @Test
    void testExecute_typicalSubmission_shouldSucceed() throws Exception {
        loginAsStudent(MATCHING_STUDENT_USER_ID);
        String[] paramsSuccessfulSubmission = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name(),
        };

        JsonResult response = getJsonResult(getAction(paramsSuccessfulSubmission));
        MessageOutput output = (MessageOutput) response.getOutput();
        assertEquals("Successful", output.getMessage());
        verify(mockLogic).createFeedbackSessionLog(eq(fsaCourse1), eq(student1InCourse1),
                eq(FeedbackSessionLogType.SUBMISSION), any());
    }

    @Test
    void testExecute_missingFeedbackSession_shouldFail() {
        String[] paramsMissingFeedbackSession = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, "00000000-0000-0000-0000-000000000000",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name(),
        };

        assertThrows(teammates.ui.webapi.InvalidHttpParameterException.class,
                () -> getAction(paramsMissingFeedbackSession).execute());
    }

    @Test
    void testExecute_notEnoughParameters_shouldFail() {
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id);
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.name());
    }

    @Test
    void testExecute_invalidLogType_shouldFail() {
        String invalidLogType = "invalid log type";
        String[] paramsInvalid = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsaCourse1Id,
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, invalidLogType,
        };
        verifyHttpParameterFailure(paramsInvalid);
    }

}
