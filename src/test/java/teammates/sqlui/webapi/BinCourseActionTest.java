package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.webapi.BinCourseAction;

/**
 * SUT: {@link BinCourseAction}.
 */
public class BinCourseActionTest extends BaseActionTest<BinCourseAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    void testExecute_courseDoesNotExist_throwsEntityDoesNotExistException() throws EntityDoesNotExistException {
        String courseId = "invalid-course-id";

        when(mockLogic.getCourse(courseId)).thenReturn(null);
        when(mockLogic.moveCourseToRecycleBin(courseId)).thenThrow(new EntityDoesNotExistException(""));

        String[] params = {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_courseExists_success() throws EntityDoesNotExistException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        course.setCreatedAt(Instant.parse("2021-01-01T00:00:00Z"));

        Instant expectedDeletedAt = Instant.parse("2022-01-01T00:00:00Z");
        course.setDeletedAt(expectedDeletedAt);

        when(mockLogic.moveCourseToRecycleBin(course.getId())).thenReturn(course);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        BinCourseAction action = getAction(params);
        CourseData actionOutput = (CourseData) getJsonResult(action).getOutput();

        assertEquals(JsonUtils.toJson(new CourseData(course)), JsonUtils.toJson(actionOutput));
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
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, true);
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
