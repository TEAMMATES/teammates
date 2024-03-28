package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseUpdateRequest;
import teammates.ui.webapi.UpdateCourseAction;

/**
 * SUT: {@link UpdateCourseAction}.
 */
public class UpdateCourseActionTest extends BaseActionTest<UpdateCourseAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    void testExecute_courseDoesNotExist_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException, InvalidParametersException {
        Course course = new Course("invalid-course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        String expectedCourseName = "new-name";
        String expectedTimeZone = "GMT";

        when(mockLogic.updateCourse(course.getId(), expectedCourseName, expectedTimeZone))
                .thenThrow(new EntityDoesNotExistException(""));

        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setCourseName(expectedCourseName);
        request.setTimeZone(expectedTimeZone);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyEntityNotFound(request, params);
    }

    @Test
    void testExecute_courseExists_success() throws EntityDoesNotExistException, InvalidParametersException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        course.setCreatedAt(Instant.parse("2022-01-01T00:00:00Z"));

        String expectedCourseName = "new-name";
        String expectedTimeZone = "GMT";

        Course expectedCourse = new Course(course.getId(), expectedCourseName, expectedTimeZone, course.getInstitute());
        expectedCourse.setCreatedAt(course.getCreatedAt());

        when(mockLogic.updateCourse(course.getId(), expectedCourseName, expectedTimeZone)).thenReturn(expectedCourse);

        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setCourseName(expectedCourseName);
        request.setTimeZone(expectedTimeZone);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        UpdateCourseAction action = getAction(request, params);
        CourseData actionOutput = (CourseData) getJsonResult(action).getOutput();

        assertEquals(JsonUtils.toJson(new CourseData(expectedCourse)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_invalidCourseName_throwsInvalidHttpRequestBodyException()
            throws EntityDoesNotExistException, InvalidParametersException {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        String expectedCourseName = ""; // invalid
        String expectedTimeZone = "GMT";

        when(mockLogic.updateCourse(course.getId(), expectedCourseName, expectedTimeZone))
                .thenThrow(new InvalidParametersException(""));

        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setCourseName(expectedCourseName);
        request.setTimeZone(expectedTimeZone);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpRequestBodyFailure(request, params);
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
