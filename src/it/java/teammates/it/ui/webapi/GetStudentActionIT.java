package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.StudentData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetStudentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionIT extends BaseActionIT<GetStudentAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Typical Success Case logged in as instructor, Registered Student");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        GetStudentAction getStudentAction = getAction(params);
        JsonResult actionOutput = getJsonResult(getStudentAction);
        StudentData response = (StudentData) actionOutput.getOutput();

        assertEquals(student.getName(), response.getName());

        logoutUser();
        loginAsStudent(student.getGoogleId());

        ______TS("Typical Success Case logged in as student, Registered Student");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        getStudentAction = getAction(params);
        actionOutput = getJsonResult(getStudentAction);
        response = (StudentData) actionOutput.getOutput();

        assertEquals(student.getName(), response.getName());

        ______TS("Typical Success Case with Unregistered Student");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, null,
        };

        getStudentAction = getAction(params);
        actionOutput = getJsonResult(getStudentAction);
        response = (StudentData) actionOutput.getOutput();

        assertEquals(student.getName(), response.getName());
        assertNull(response.getComments());
        assertNull(response.getJoinState());
        assertEquals(student.getCourse().getInstitute(), response.getInstitute());

        ______TS("Student is non existent");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "does-not-exist@teammates.tmt",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);

        assertEquals("No student found", enfe.getMessage());

        logoutUser();

        ______TS("Typical Success Case logged in as admin, Registered Student");
        loginAsAdmin();

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        getStudentAction = getAction(params);
        actionOutput = getJsonResult(getStudentAction);
        response = (StudentData) actionOutput.getOutput();

        assertEquals(student.getName(), response.getName());
        assertEquals(student.getRegKey(), response.getKey());
        assertEquals(student.getGoogleId(), response.getGoogleId());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Only students of the same course with correct privilege can access");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyAccessibleForStudentsOfTheSameCourse(course, params);

        logoutUser();

        ______TS("Only instructors of the same course with correct privilege can access");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        loginAsInstructor(instructor.getGoogleId());

        verifyInaccessibleForInstructorsOfOtherCourses(course, params);

        ______TS("Unregistered Student can access with key");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.REGKEY, "does-not-exist-key",
        };

        verifyInaccessibleForUnregisteredUsers(params);
    }

}
