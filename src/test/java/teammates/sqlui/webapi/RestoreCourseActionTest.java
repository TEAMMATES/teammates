package teammates.sqlui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.RestoreCourseAction;

/**
 * SUT: {@link RestoreCourseAction}.
 */
public class RestoreCourseActionTest extends BaseActionTest<RestoreCourseAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testExecute_courseDoesNotExist_throwsEntityDoesNotExistException() throws EntityDoesNotExistException {
        String courseId = "invalid-course-id";

        when(mockLogic.getCourse(courseId)).thenReturn(null);
        doThrow(new EntityDoesNotExistException("")).when(mockLogic).restoreCourseFromRecycleBin(courseId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_courseExists_success() throws EntityDoesNotExistException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        when(mockLogic.getCourse(course.getId())).thenReturn(course);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        RestoreCourseAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("The course " + course.getId() + " has been restored.", actionOutput.getMessage());
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
