package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.CourseData;
import teammates.ui.webapi.GetCourseAction;

/**
 * SUT: {@link GetCourseAction}.
 */
public class GetCourseActionTest extends BaseActionTest<GetCourseAction> {

    String googleId = "user-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testSpecificAccessControl_courseDoesNotExist_cannotAccess() {
        loginAsInstructor(googleId);
        when(mockLogic.getCourse("course-id")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_asInstructor_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt", false, "", null, null);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_asUnregisteredInstructorWithRegKey_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt", false, "", null, null);

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, instructor.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_asStudent_canAccess() {
        loginAsStudent(googleId);

        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student = new Student(course, "name", "studen_email@tm.tmt", "student comments");

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentByGoogleId(course.getId(), googleId)).thenReturn(student);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notLoggedIn_cannotAccess() {
        logoutUser();

        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyCannotAccess(params);
    }

    @Test
    void testExecute_invalidEntityType_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.ENTITY_TYPE, "invalid-entity-type",
        };
        verifyCannotAccess(params);
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_notEnoughParameters_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, null,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_courseDoesNotExist_throwsEntityNotFoundException() {
        when(mockLogic.getCourse("course-id")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_asInstructor_success() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        course.setCreatedAt(Instant.parse("2022-01-01T00:00:00Z"));

        when(mockLogic.getCourse(course.getId())).thenReturn(course);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        GetCourseAction getCourseAction = getAction(params);
        CourseData actionOutput = (CourseData) getJsonResult(getCourseAction).getOutput();
        assertEquals(JsonUtils.toJson(new CourseData(course)), JsonUtils.toJson(actionOutput));
    }

    @Test
    void testExecute_asStudentHideCreatedAtAndDeletedAt_success() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        course.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        when(mockLogic.getCourse(course.getId())).thenReturn(course);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseAction getCourseAction = getAction(params);
        CourseData actionOutput = (CourseData) getJsonResult(getCourseAction).getOutput();

        Course expectedCourse = course;
        expectedCourse.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        expectedCourse.setDeletedAt(Instant.ofEpochMilli(0));

        assertEquals(JsonUtils.toJson(new CourseData(expectedCourse)), JsonUtils.toJson(actionOutput));
    }
}
