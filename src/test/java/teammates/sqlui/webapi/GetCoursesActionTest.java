package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CoursesData;
import teammates.ui.webapi.GetCoursesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCoursesAction}.
 */
public class GetCoursesActionTest extends BaseActionTest<GetCoursesAction> {
    private Instructor stubInstructor;
    private List<Instructor> stubInstructorList;
    private Course stubCourse;
    private List<Course> stubCourseList;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        stubInstructor = getTypicalInstructor();
        stubInstructorList = new ArrayList<>();
        stubInstructorList.add(stubInstructor);
        stubCourse = getTypicalCourse();
        stubCourse.setCreatedAt(Instant.ofEpochMilli(1));
        stubCourseList = new ArrayList<>();
        stubCourseList.add(stubCourse);
    }

    private List<Course> prepareCourseStubList() {
        Course stubCourse = getTypicalCourse();
        stubCourse.setCreatedAt(Instant.ofEpochMilli(1));
        List<Course> stubCourseList = new ArrayList<>();
        stubCourseList.add(stubCourse);
        return stubCourseList;
    }

    @Test
    void testExecute_withInstructorAndActiveCourses_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getInstructorsForGoogleId(stubInstructor.getGoogleId()))
                .thenReturn(stubInstructorList);
        when(mockLogic.getCoursesForInstructors(argThat(
                argument ->
                        Objects.equals(argument.get(0).getGoogleId(),
                        stubInstructor.getGoogleId())))).thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        assertEquals(stubCourseList.size(), data.getCourses().size());
        assertEquals(stubCourseList.get(0).getId(), data.getCourses().get(0).getCourseId());
        assertEquals(stubInstructor.getPrivileges().getCourseLevelPrivileges(), data.getCourses().get(0).getPrivileges());
    }

    @Test
    void testExecute_withInstructorAndArchivedCourses_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ARCHIVED,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        assertEquals(0, data.getCourses().size());

    }

    @Test
    void testExecute_withInstructorAndSoftDeletedCourses_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getInstructorsForGoogleId(stubInstructor.getGoogleId()))
                .thenReturn(stubInstructorList);
        when(mockLogic.getSoftDeletedCoursesForInstructors(argThat(
                argument -> Objects.equals(argument.get(0).getGoogleId(),
                                stubInstructor.getGoogleId())))).thenReturn(stubCourseList);

        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        assertEquals(stubCourseList.size(), data.getCourses().size());
        assertEquals(stubCourseList.get(0).getId(), data.getCourses().get(0).getCourseId());
        assertEquals(stubInstructor.getPrivileges().getCourseLevelPrivileges(), data.getCourses().get(0).getPrivileges());
    }

    @Test
    void testExecute_withInstructorAndInvalidCourseStatus_throwsException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, "invalid",
        };
        verifyHttpParameterFailure(params);

        String[] params2 = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, null,
        };

        verifyHttpParameterFailure(params2);
    }

    @Test
    void testExecute_withStudentEntityType_success() {
        loginAsStudent("student");
        when(mockLogic.getCoursesForStudentAccount("student")).thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        assertEquals(1, data.getCourses().size());
        assertEquals(stubCourse.getId(), data.getCourses().get(0).getCourseId());
        assertEquals(0, data.getCourses().get(0).getDeletionTimestamp());
    }

    @Test
    void testExecute_withInvalidEntityType_throwsException() {
        loginAsStudent("student");
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, "invalid",
        };
        verifyHttpParameterFailure(params);

        String[] params2 = {
                Const.ParamsNames.ENTITY_TYPE, null,
        };
        verifyHttpParameterFailure(params2);
    }

    @Test
    void testSpecificAccessControl_instructor_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_success() {
        loginAsStudent("student");
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_admin_cannotAccess() {
        loginAsAdmin();
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorAndNotLoggedIn_cannotAccess() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyCannotAccess(params);
    }
}
