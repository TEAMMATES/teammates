package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;
import teammates.ui.webapi.GetCoursesAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCoursesAction}.
 */
public class GetCoursesActionIT extends BaseActionIT<GetCoursesAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        this.typicalBundle = loadSqlDataBundle("/GetCoursesActionIT.json");
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // See separated test cases below.
    }

    @Test
    public void testGetCoursesAction_withNoParameter_shouldThrowHttpParameterException() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure();
    }

    @Test
    public void testGetCoursesAction_withInvalidEntityType_shouldReturnBadResponse() {
        String[] params = new String[] { Const.ParamsNames.ENTITY_TYPE, "invalid_entity_type" };
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure(params);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndNoCourseStatus_shouldThrowParameterFailure() {
        String[] params = { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR, };
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure(params);
    }

    @Test
    public void testGetCoursesAction_withInvalidCourseStatus_shouldReturnBadResponse() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, "Invalid status",
        };

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        verifyHttpParameterFailure(params);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndActiveCourses_shouldReturnCorrectCourses() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE,
        };
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        CoursesData courses = getValidCourses(params);
        assertEquals(3, courses.getCourses().size());
        Course expectedCourse1 = typicalBundle.courses.get("typicalCourse1");
        Course expectedCourse2 = typicalBundle.courses.get("typicalCourse2");
        Course expectedCourse3 = typicalBundle.courses.get("typicalCourse4");
        verifySameCourseData(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseData(courses.getCourses().get(1), expectedCourse2);
        verifySameCourseData(courses.getCourses().get(2), expectedCourse3);
    }

    @Test
    public void testGetCoursesAction_withInstructorEntityTypeAndSoftDeletedCourses_shouldReturnCorrectCourses() {
        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED,
        };

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        CoursesData courses = getValidCourses(params);
        assertEquals(2, courses.getCourses().size());
        Course expectedCourse1 = typicalBundle.courses.get("typicalCourse3");
        Course expectedCourse2 = typicalBundle.courses.get("typicalCourse5");
        verifySameCourseData(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseData(courses.getCourses().get(1), expectedCourse2);
    }

    @Test
    public void testGetCoursesAction_withStudentEntityType_shouldReturnCorrectCourses() {
        String[] params = { Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT };
        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student.getGoogleId());

        CoursesData courses = getValidCourses(params);
        courses.getCourses().sort((c1, c2) -> c1.getCourseId().compareTo(c2.getCourseId()));
        assertEquals(3, courses.getCourses().size());
        Course expectedCourse1 = typicalBundle.courses.get("typicalCourse1");
        Course expectedCourse2 = typicalBundle.courses.get("typicalCourse2");
        Course expectedCourse3 = typicalBundle.courses.get("typicalCourse4");

        verifySameCourseDataStudent(courses.getCourses().get(0), expectedCourse1);
        verifySameCourseDataStudent(courses.getCourses().get(1), expectedCourse2);
        verifySameCourseDataStudent(courses.getCourses().get(2), expectedCourse3);
    }

    private void verifySameCourseData(CourseData actualCourse, Course expectedCourse) {
        assertEquals(actualCourse.getCourseId(), expectedCourse.getId());
        assertEquals(actualCourse.getCourseName(), expectedCourse.getName());
        assertEquals(actualCourse.getCreationTimestamp(), expectedCourse.getCreatedAt().toEpochMilli());
        if (expectedCourse.getDeletedAt() != null) {
            assertEquals(actualCourse.getDeletionTimestamp(), expectedCourse.getDeletedAt().toEpochMilli());
        }
        assertEquals(actualCourse.getTimeZone(), expectedCourse.getTimeZone());
    }

    private void verifySameCourseDataStudent(CourseData actualCourse, Course expectedCourse) {
        assertEquals(actualCourse.getCourseId(), expectedCourse.getId());
        assertEquals(actualCourse.getCourseName(), expectedCourse.getName());
        assertEquals(actualCourse.getCreationTimestamp(), expectedCourse.getCreatedAt().toEpochMilli());
        if (expectedCourse.getDeletedAt() != null) {
            assertEquals(actualCourse.getDeletionTimestamp(), expectedCourse.getDeletedAt().toEpochMilli());
        }
        assertEquals(actualCourse.getTimeZone(), expectedCourse.getTimeZone());
    }

    private CoursesData getValidCourses(String... params) {
        GetCoursesAction getCoursesAction = getAction(params);
        JsonResult result = getJsonResult(getCoursesAction);
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

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");

        ______TS("Login as instructor, only instructor entity type can access");
        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(instructorParams);
        verifyCannotAccess(studentParams);

        ______TS("Login as student, only student entity type can access");
        loginAsStudent(student.getGoogleId());
        verifyCanAccess(studentParams);
        verifyCannotAccess(instructorParams);
    }

}
