package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteCourseAction;

/**
 * SUT: {@link DeleteCourseAction}.
 */
public class DeleteCourseActionTest extends BaseActionTest<DeleteCourseAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testExecute_courseDoesNotExist_failSilently() {
        String courseId = "course-id";

        when(mockLogic.getCourse(courseId)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        DeleteCourseAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("OK", actionOutput.getMessage());
    }

    @Test
    void testExecute_courseExists_success() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        when(mockLogic.getCourse(course.getId())).thenReturn(course);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        DeleteCourseAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("OK", actionOutput.getMessage());
    }

    @Test
    void testExecute_invalidCourseId_failSilently() {
        when(mockLogic.getCourse("invalid-course-id")).thenReturn(null);
        String[] params = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };

        DeleteCourseAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("OK", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivileges());

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_COURSE, true);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
        };
        loginAsStudent(googleId);
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
