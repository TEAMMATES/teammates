package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetInstructorAction;

/**
 * SUT: {@link GetInstructorAction}.
 */
public class GetInstructorActionTest extends BaseActionTest<GetInstructorAction> {

    Course course;
    FeedbackSession feedbackSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        loginAsInstructor("user-id");
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.INTENT, "invalid-intent",
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_unknownIntent_throwsInvalidHttpParameterException() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_instructorSubmission_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        assertEquals(JsonUtils.toJson(new InstructorData(instructor)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_instructorSubmissionUnregistered_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, instructor.getRegKey(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        assertEquals(JsonUtils.toJson(new InstructorData(instructor)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_instructorResult_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        assertEquals(JsonUtils.toJson(new InstructorData(instructor)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_instructorResultUnregistered_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, instructor.getRegKey(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        assertEquals(JsonUtils.toJson(new InstructorData(instructor)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_fullDetail_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        assertEquals(JsonUtils.toJson(new InstructorData(instructor)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_fullDetailWithAccount_success() {
        Account account = new Account("google-id", "name", "email@tm.tmt");
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        instructor.setAccount(account);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        InstructorData actionOutput = (InstructorData) getJsonResult(getInstructorAction).getOutput();
        InstructorData expected = new InstructorData(instructor);
        expected.setGoogleId("google-id");
        assertEquals(JsonUtils.toJson(expected), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_fullDetailUnregistered_success() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, instructor.getRegKey(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testSpecificAccessControl_loggedInAsInstuctor_canAccess() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedInAsInstuctorFromAnotherCourse_cannotAccess() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, "different-course",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedInAsInstuctorFullDetail_canAccess() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, "different-course",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notLoggedInFullDetail_cannotAccess() {
        logoutUser();
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByGoogleId(course.getId(), "user-id")).thenReturn(instructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, "different-course",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyCannotAccess(params);
    }

}
