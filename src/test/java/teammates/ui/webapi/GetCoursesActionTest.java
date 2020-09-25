package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;

/**
 * SUT: {@link GetCoursesAction}.
 */
public class GetCoursesActionTest extends BaseActionTest<GetCoursesAction> {

    private DataBundle testData;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    public void prepareTestData() {
        testData = loadDataBundle("/GetCoursesActionTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // See separated test cases below.
    }

    @Test
    public void testGetCoursesAction_withNoParameter_shouldThrowHttpParameterException() {
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);
        verifyHttpParameterFailure();
    }

    @Test
    public void testGetCoursesAction_withInvalidEntityType_shouldReturnBadResponse() {
        String[] params = new String[] { Const.ParamsNames.ENTITY_TYPE, "invalid_entity_type" };
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);
        assertBadRequest(params);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndNoCourseStatus_shouldThrowParameterFailure() {
        String[] params = { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR, };
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);
        verifyHttpParameterFailure(params);
    }

    @Test
    public void testGetCoursesAction_withInvalidCourseStatus_shouldReturnBadResponse() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, "Invalid status",
        };

        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);
        assertBadRequest(params);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndActiveCourses_shouldReturnCorrectCourses() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);

        CoursesData courses = getValidCourses(params);
        assertEquals(2, courses.getCourses().size());
        CourseAttributes expectedCourse1 = testData.courses.get("typicalCourse1");
        CourseAttributes expectedCourse2 = testData.courses.get("typicalCourse2");
        verifySameCourseData(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseData(courses.getCourses().get(1), expectedCourse2);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndArchivedCourses_shouldReturnCorrectCourses() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ARCHIVED,
        };

        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);

        CoursesData courses = getValidCourses(params);
        assertEquals(1, courses.getCourses().size());
        CourseAttributes expectedCourse = testData.courses.get("typicalCourse4");
        verifySameCourseData(courses.getCourses().get(0), expectedCourse);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndSoftDeletedCourses_shouldReturnCorrectCourses() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED,
        };

        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.googleId);

        CoursesData courses = getValidCourses(params);
        assertEquals(2, courses.getCourses().size());
        CourseAttributes expectedCourse1 = testData.courses.get("typicalCourse3");
        CourseAttributes expectedCourse2 = testData.courses.get("typicalCourse5");
        verifySameCourseData(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseData(courses.getCourses().get(1), expectedCourse2);
    }

    @Test
    public void testGetCoursesAction_withStudentEntityType_shouldReturnCorrectCourses() {
        String[] params = { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT };
        StudentAttributes student = testData.students.get("student1InCourse1");
        loginAsStudent(student.googleId);

        CoursesData courses = getValidCourses(params);
        assertEquals(3, courses.getCourses().size());
        CourseAttributes expectedCourse1 = testData.courses.get("typicalCourse1");
        CourseAttributes expectedCourse2 = testData.courses.get("typicalCourse2");
        CourseAttributes expectedCourse3 = testData.courses.get("typicalCourse4");

        verifySameCourseDataStudent(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseDataStudent(courses.getCourses().get(1), expectedCourse2);
        verifySameCourseDataStudent(courses.getCourses().get(2), expectedCourse3);
    }

    private void verifySameCourseData(CourseData actualCourse, CourseAttributes expectedCourse) {
        assertEquals(actualCourse.getCourseId(), expectedCourse.getId());
        assertEquals(actualCourse.getCourseName(), expectedCourse.getName());
        assertEquals(actualCourse.getCreationTimestamp(), expectedCourse.getCreatedAt().toEpochMilli());
        if (expectedCourse.getDeletedAt() != null) {
            assertEquals(actualCourse.getDeletionTimestamp(), expectedCourse.getDeletedAt().toEpochMilli());
        }
        assertEquals(actualCourse.getTimeZone(), expectedCourse.getTimeZone().getId());
    }

    private void verifySameCourseDataStudent(CourseData actualCourse, CourseAttributes expectedCourse) {
        assertEquals(actualCourse.getCourseId(), expectedCourse.getId());
        assertEquals(actualCourse.getCourseName(), expectedCourse.getName());
        assertEquals(actualCourse.getCreationTimestamp(), 0);
        assertEquals(actualCourse.getDeletionTimestamp(), 0);
        assertEquals(actualCourse.getTimeZone(), expectedCourse.getTimeZone().getId());
    }

    private void assertBadRequest(String... params) {
        GetCoursesAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
    }

    private CoursesData getValidCourses(String... params) {
        GetCoursesAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        return (CoursesData) result.getOutput();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        String[] studentParams = new String[] { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT, };
        String[] instructorParams = new String[] { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR, };

        ______TS("Without login or registration, cannot access");
        verifyInaccessibleWithoutLogin(studentParams);
        verifyInaccessibleWithoutLogin(instructorParams);
        verifyInaccessibleForUnregisteredUsers(studentParams);
        verifyInaccessibleForUnregisteredUsers(instructorParams);

        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        StudentAttributes student = testData.students.get("student1InCourse1");

        ______TS("Login as instructor, only instructor entity type can access");
        loginAsInstructor(instructor.googleId);
        verifyCanAccess(instructorParams);
        verifyCannotAccess(studentParams);

        ______TS("Login as student, only student entity type can access");
        loginAsStudent(student.googleId);
        verifyCanAccess(studentParams);
        verifyCannotAccess(instructorParams);
    }
}
