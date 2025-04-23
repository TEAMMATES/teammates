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
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;
import teammates.ui.webapi.GetCoursesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCoursesAction}.
 */
public class GetCoursesActionTest extends BaseActionTest<GetCoursesAction> {
    private Instructor stubInstructor;
    private List<Instructor> stubInstructorList;
    private List<Course> stubCourseList;
    private CoursesData expectedCoursesData;

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
        Course stubCourse = getTypicalCourse();
        stubCourse.setCreatedAt(Instant.ofEpochMilli(1));
        stubCourseList = new ArrayList<>();
        stubCourseList.add(stubCourse);
        expectedCoursesData = new CoursesData(stubCourseList);
    }

    @Test
    void testExecute_withInstructorAndActiveCourses_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getInstructorsForGoogleId(stubInstructor.getGoogleId()))
                .thenReturn(stubInstructorList);
        when(mockLogic.getCoursesForInstructors(argThat(
                argument -> Objects.equals(argument.get(0).getGoogleId(),
                        stubInstructor.getGoogleId()))))
                .thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        verifySameCoursesData(expectedCoursesData, data, false);
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
                        stubInstructor.getGoogleId()))))
                .thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED,
        };
        GetCoursesAction action = getAction(params);
        JsonResult result = action.execute();
        CoursesData data = (CoursesData) result.getOutput();
        verifySameCoursesData(expectedCoursesData, data, false);
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
        verifySameCoursesData(expectedCoursesData, data, true);
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

    private void verifySameCoursesData(CoursesData expectedCourses, CoursesData actualCourses, boolean isStudent) {
        if (expectedCourses.getCourses().size() != actualCourses.getCourses().size()) {
            fail("Course list size does not match");
        }
        for (int i = 0; i < expectedCourses.getCourses().size(); i++) {
            if (isStudent) {
                verifySameCourseDataStudent(expectedCourses.getCourses().get(i), actualCourses.getCourses().get(i));
            } else {
                verifySameCourseData(expectedCourses.getCourses().get(i), actualCourses.getCourses().get(i));
            }
        }
    }

    private void verifySameCourseDataStudent(CourseData expectedCourseData, CourseData actualCourseData) {
        assertEquals(expectedCourseData.getCourseId(), actualCourseData.getCourseId());
        assertEquals(expectedCourseData.getCourseName(), actualCourseData.getCourseName());
        assertEquals(expectedCourseData.getCreationTimestamp(), actualCourseData.getCreationTimestamp());
        assertEquals(0, actualCourseData.getDeletionTimestamp());
        assertEquals(expectedCourseData.getTimeZone(), actualCourseData.getTimeZone());
    }

    private void verifySameCourseData(CourseData expectedCourseData, CourseData actualCourseData) {
        assertEquals(expectedCourseData.getCourseId(), actualCourseData.getCourseId());
        assertEquals(expectedCourseData.getCourseName(), actualCourseData.getCourseName());
        assertEquals(expectedCourseData.getCreationTimestamp(), actualCourseData.getCreationTimestamp());
        assertEquals(expectedCourseData.getDeletionTimestamp(), actualCourseData.getDeletionTimestamp());
        assertEquals(expectedCourseData.getTimeZone(), actualCourseData.getTimeZone());
    }

    @Test
    void testAccessControl() {
        String[] paramsInstructors = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyAnyNonMasqueradingInstructorCanAccess(stubCourseList.get(0), paramsInstructors);

        String[] paramsStudent = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyStudentsCanAccess(paramsStudent);

        String[] paramsAdmin = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyAdminsCannotAccess(paramsAdmin);
    }

    @Test
    void testSpecificAccessControl_loginUserAndEntityMismatch_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCannotAccess(params);

        logoutUser();
        loginAsStudent("student");
        String[] params2 = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        verifyCannotAccess(params2);
    }
}
