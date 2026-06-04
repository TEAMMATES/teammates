package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.StudentData;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionIT extends BaseActionIT<GetStudentAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
                Const.ParamsNames.USER_ID, student.getId().toString(),
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
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        getStudentAction = getAction(params);
        actionOutput = getJsonResult(getStudentAction);
        response = (StudentData) actionOutput.getOutput();

        assertEquals(student.getName(), response.getName());

        ______TS("Typical Success Case with Unregistered Student");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
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
                Const.ParamsNames.USER_ID, UUID.nameUUIDFromBytes("does-not-exist"
                        .getBytes(StandardCharsets.UTF_8)).toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);

        assertEquals("No student found", enfe.getMessage());

        logoutUser();

        ______TS("Typical Success Case logged in as admin, Registered Student");
        loginAsAdmin();

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.USER_ID, student.getId().toString(),
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
                Const.ParamsNames.USER_ID, student.getId().toString(),
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
