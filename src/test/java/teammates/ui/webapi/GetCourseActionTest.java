package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link GetCourseAction}.
 */
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
    @Override
    protected void testExecute() throws Exception {
        //See test cases below
    }

    @Test
    protected void testExecute_typicalUsage_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes expectedCourse = logic.getCourse(instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("typical success case for instructor");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        GetCourseAction getCourseAction = getAction(params);
        JsonResult response = getJsonResult(getCourseAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CourseData courseData = (CourseData) response.getOutput();

        assertEquals(expectedCourse.getId(), courseData.getCourseId());
        assertEquals(expectedCourse.getName(), courseData.getCourseName());
        assertEquals(expectedCourse.getTimeZone().getId(), courseData.getTimeZone());

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        expectedCourse = logic.getCourse(student1OfCourse1.getCourse());
        loginAsStudent(student1OfCourse1.googleId);

        ______TS("typical success case for student");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, student1OfCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        getCourseAction = getAction(params);
        response = getJsonResult(getCourseAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        courseData = (CourseData) response.getOutput();

        assertEquals(expectedCourse.getId(), courseData.getCourseId());
        assertEquals(expectedCourse.getName(), courseData.getCourseName());
        assertEquals(expectedCourse.getTimeZone().getId(), courseData.getTimeZone());
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1OfCourse1.googleId);

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        testNonExistentCourse();

        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1OfCourse1.googleId);

        testNonExistentCourse();
    }

    private void testNonExistentCourse() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        GetCourseAction getCourseAction = getAction(params);
        JsonResult response = getJsonResult(getCourseAction);
        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
        assertEquals("No course with id: fake-course", messageOutput.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        //see test cases below
    }

    @Test
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

    @Test
    protected void testAccessControl_testInstructorAccess_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
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

    @Test
    protected void testAccessControl_loggedInEntityBothInstructorAndStudent_shouldBeAccessible()
            throws InvalidParametersException, EntityDoesNotExistException,
            EntityAlreadyExistsException {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes typicalCourse2 = typicalBundle.courses.get("typicalCourse2");

        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        logic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student1InCourse2.getCourse(), student1InCourse2.email)
                        .withGoogleId(instructor1OfCourse1.googleId)
                        .build());

        loginAsStudentInstructor(instructor1OfCourse1.googleId);

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
