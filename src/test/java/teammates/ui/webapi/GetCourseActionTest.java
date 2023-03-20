package teammates.ui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.EmailSender;
import teammates.logic.api.LogsProcessor;
import teammates.logic.api.RecaptchaVerifier;
import teammates.logic.api.TaskQueuer;
import teammates.logic.api.UserProvision;
import teammates.sqllogic.api.Logic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.test.MockHttpServletRequest;
import teammates.ui.output.CourseData;

/**
 * SUT: {@link GetCourseAction}.
 */
@Ignore
public class GetCourseActionTest extends BaseActionTest<GetCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    public void testExecute_success() {
        Course course = generateTypicalCourse();

        GetCourseAction action = generateGetCourseAction();
        when(action.logic.getCourse(course.getId())).thenReturn(null);
        when(action.sqlLogic.getCourse(course.getId())).thenReturn(course);

        JsonResult response = action.execute();

        verify(action.logic, times(1)).getCourse(course.getId());
        verify(action.sqlLogic, times(1)).getCourse(course.getId());

        CourseData courseData = (CourseData) response.getOutput();

        assertEquals(course.getId(), courseData.getCourseId());
        assertEquals(course.getName(), courseData.getCourseName());
        assertEquals(course.getTimeZone(), courseData.getTimeZone());
    }

    private Course generateTypicalCourse() {
        Course c = new Course("test-courseId", "test-courseName", "test-courseTimeZone", "test-courseInstitute");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return c;
    }

    private Instructor generateTypicalCoOwnerInstructor() {
        Course course = generateTypicalCourse();
        InstructorPermissionRole instructorPermissionRole = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges(instructorPermissionRole.getRoleName());

        return new Instructor(
                course, "test-instructorName", "test@test.com", true,
                "test-instructorDisplayName", instructorPermissionRole, instructorPrivileges);
    }

    private GetCourseAction generateGetCourseAction() {
        // Create mock classes
        TaskQueuer mockTaskQueuer = mock(TaskQueuer.class);
        EmailSender mockEmailSender = mock(EmailSender.class);
        LogsProcessor mockLogsProcessor = mock(LogsProcessor.class);
        RecaptchaVerifier mockRecaptchaVerifier = mock(RecaptchaVerifier.class);
        UserProvision mockUserProvision = mock(UserProvision.class);

        Logic sqlLogic = mock(Logic.class);
        teammates.logic.api.Logic logic = mock(teammates.logic.api.Logic.class);

        MockHttpServletRequest req = new MockHttpServletRequest(getRequestMethod(), getActionUri());

        String[] params = {
                Const.ParamsNames.COURSE_ID, generateTypicalCoOwnerInstructor().getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        for (int i = 0; i < params.length; i = i + 2) {
            req.addParam(params[i], params[i + 1]);
        }

        try {
            GetCourseAction action = (GetCourseAction.class).getDeclaredConstructor().newInstance();
            action.req = req;
            action.setTaskQueuer(mockTaskQueuer);
            action.setEmailSender(mockEmailSender);
            action.setLogsProcessor(mockLogsProcessor);
            action.setUserProvision(mockUserProvision);
            action.setRecaptchaVerifier(mockRecaptchaVerifier);
            action.sqlLogic = sqlLogic;
            action.logic = logic;

            return action;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // OLD TESTS:
    @Test
    @Override
    protected void testExecute() {
        //See test cases below
    }

    @Test
    protected void testExecute_typicalUsage_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes expectedCourse = logic.getCourse(instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical success case for instructor");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        GetCourseAction getCourseAction = getAction(params);
        JsonResult response = getJsonResult(getCourseAction);

        CourseData courseData = (CourseData) response.getOutput();

        assertEquals(expectedCourse.getId(), courseData.getCourseId());
        assertEquals(expectedCourse.getName(), courseData.getCourseName());
        assertEquals(expectedCourse.getTimeZone(), courseData.getTimeZone());

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        expectedCourse = logic.getCourse(student1OfCourse1.getCourse());
        loginAsStudent(student1OfCourse1.getGoogleId());

        ______TS("typical success case for student");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, student1OfCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        getCourseAction = getAction(params);
        response = getJsonResult(getCourseAction);

        courseData = (CourseData) response.getOutput();

        assertEquals(expectedCourse.getId(), courseData.getCourseId());
        assertEquals(expectedCourse.getName(), courseData.getCourseName());
        assertEquals(expectedCourse.getTimeZone(), courseData.getTimeZone());
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1OfCourse1.getGoogleId());

        verifyHttpParameterFailure();
    }

    @Test (enabled = false)
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        testNonExistentCourse();

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1OfCourse1.getGoogleId());

        testNonExistentCourse();
    }

    private void testNonExistentCourse() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("No course with id: fake-course", enfe.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() {
        //see test cases below
    }

    @Test (enabled = false)
    protected void testAccessControl_invalidParameterValues_shouldFail() {
        ______TS("non-existent course");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "not-exist",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        assertNull(logic.getCourse("not-exist"));

        verifyCannotAccess(submissionParams);

        ______TS("non-existent entitytype");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, "no-entity",
        };

        verifyCannotAccess(submissionParams);
    }

    @Test(enabled = false)
    protected void testAccessControl_testInstructorAccess_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test(enabled = false)
    protected void testAccessControl_testStudentAccess_shouldPass() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdmin(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForInstructors(submissionParams);
    }

    @Test(enabled = false)
    protected void testAccessControl_loggedInEntityBothInstructorAndStudent_shouldBeAccessible() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes typicalCourse2 = typicalBundle.courses.get("typicalCourse2");

        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        logic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student1InCourse2.getCourse(), student1InCourse2.getEmail())
                        .withGoogleId(instructor1OfCourse1.getGoogleId())
                        .build());

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("StudentInstructor can access course with only instructor privileges");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(params);

        ______TS("StudentInstructor can access course with only student privileges");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyCanAccess(params);

        ______TS("StudentInstructor cannot access student identity's course with instructor privileges");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCannotAccess(params);

        ______TS("StudentInstructor cannot access instructor identity's course with student privileges");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyCannotAccess(params);
    }

}
