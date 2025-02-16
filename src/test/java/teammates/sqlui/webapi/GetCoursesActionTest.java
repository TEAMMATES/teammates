package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    private InstructorStub prepareInstructorStub() {
        Instructor stubInstructor = getTypicalInstructor();
        List<Instructor> stubInstructorList = new ArrayList<>();
        stubInstructorList.add(stubInstructor);
        return new InstructorStub(stubInstructorList, stubInstructor);
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
        InstructorStub stubInstructorObject = prepareInstructorStub();
        List<Course> stubCourseList = prepareCourseStubList();

        loginAsInstructor(stubInstructorObject.getInstructorStub().getGoogleId());
        when(mockLogic.getInstructorsForGoogleId(stubInstructorObject.getInstructorStub().getGoogleId()))
                .thenReturn(stubInstructorObject.getInstructorListStub());
        when(mockLogic.getCoursesForInstructors(argThat(
                argument ->
                        Objects.equals(argument.get(0).getGoogleId(),
                        stubInstructorObject.getInstructorStub().getGoogleId())))).thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        GetCoursesAction a = getAction(params);
        JsonResult result = a.execute();
        CoursesData x = (CoursesData) result.getOutput();
        assertEquals(stubCourseList.size(), x.getCourses().size());
        assertEquals(stubCourseList.get(0).getId(), x.getCourses().get(0).getCourseId());
    }

    @Test
    void testExecute_withInstructorAndArchivedCourses_success() {
        Instructor stubInstructor = getTypicalInstructor();

        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ARCHIVED,
        };
        GetCoursesAction a = getAction(params);
        JsonResult result = a.execute();
        CoursesData x = (CoursesData) result.getOutput();
        assertEquals(0, x.getCourses().size());

    }

    @Test
    void testExecute_withInstructorAndSoftDeletedCourses_success() {
        InstructorStub stubInstructorObject = prepareInstructorStub();
        List<Course> stubCourseList = prepareCourseStubList();

        loginAsInstructor(stubInstructorObject.getInstructorStub().getGoogleId());
        when(mockLogic.getInstructorsForGoogleId(stubInstructorObject.getInstructorStub().getGoogleId()))
                .thenReturn(stubInstructorObject.getInstructorListStub());
        when(mockLogic.getSoftDeletedCoursesForInstructors(argThat(
                argument -> Objects.equals(argument.get(0).getGoogleId(),
                                stubInstructorObject.getInstructorStub().getGoogleId())))).thenReturn(stubCourseList);

        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED,
        };
        GetCoursesAction a = getAction(params);
        JsonResult result = a.execute();
        CoursesData x = (CoursesData) result.getOutput();
        assertEquals(stubCourseList.size(), x.getCourses().size());
        assertEquals(stubCourseList.get(0).getId(), x.getCourses().get(0).getCourseId());
    }

    @Test
    void testExecute_withInstructorAndInvalidCourseStatus_throwsException() {
        Instructor stubInstructor = getTypicalInstructor();
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
        List<Course> stubCourseList = prepareCourseStubList();
        Course stubCourse = stubCourseList.get(0);
        when(mockLogic.getCoursesForStudentAccount("student")).thenReturn(stubCourseList);
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        GetCoursesAction a = getAction(params);
        JsonResult result = a.execute();
        CoursesData x = (CoursesData) result.getOutput();
        assertEquals(1, x.getCourses().size());
        assertEquals(stubCourse.getId(), x.getCourses().get(0).getCourseId());
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
        Instructor stubInstructor = getTypicalInstructor();
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

    private static class InstructorStub {
        List<Instructor> instructorListStub;
        Instructor instructorStub;

        InstructorStub(List<Instructor> instructorListStub, Instructor instructorStub) {
            this.instructorListStub = instructorListStub;
            this.instructorStub = instructorStub;
        }

        List<Instructor> getInstructorListStub() {
            return instructorListStub;
        }

        Instructor getInstructorStub() {
            return instructorStub;
        }
    }
}
