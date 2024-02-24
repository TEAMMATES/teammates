package teammates.ui.webapi;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link ResetAccountAction}.
 */
@Ignore
public class ResetAccountActionTest extends BaseActionTest<ResetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1OfCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();

        ______TS("Failure case: no parameters supplied");

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure();
        assertEquals("Either student email or instructor email has to be specified.", ihpe.getMessage());

        ______TS("Failure case: no course id supplied");

        String[] paramsInsufficient = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
        };

        verifyHttpParameterFailure(paramsInsufficient);

        ______TS("Failure case: Instructor not exist");
        String[] invalidInstructorParams = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-exist-instructor@test.com",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidInstructorParams);
        assertEquals("Instructor does not exist.", enfe.getMessage());

        ______TS("Failure case: Student not exist");
        String[] invalidStudentParams = {
                Const.ParamsNames.STUDENT_EMAIL, "non-exist-student@test.com",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        enfe = verifyEntityNotFound(invalidStudentParams);
        assertEquals("Student does not exist.", enfe.getMessage());

        ______TS("Failure case: Course not exist");
        String[] invalidCourseParams = {
                Const.ParamsNames.STUDENT_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non exist course id",
        };

        enfe = verifyEntityNotFound(invalidCourseParams);
        assertEquals("Student does not exist.", enfe.getMessage());

        ______TS("typical success case: reset instructor account");

        String[] paramsInstructor = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        ResetAccountAction a = getAction(paramsInstructor);
        JsonResult r = getJsonResult(a);

        MessageOutput response = (MessageOutput) r.getOutput();

        InstructorAttributes instructor = logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(),
                instructor1OfCourse1.getEmail());

        assertEquals(response.getMessage(), "Account is successfully reset.");
        assertNotNull(instructor);
        assertNull(instructor.getGoogleId());
        assertNull(logic.getInstructorForGoogleId(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId()));

        ______TS("typical success case: reset student account");

        String[] paramsStudent = {
                Const.ParamsNames.STUDENT_EMAIL, student1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, student1OfCourse1.getCourse(),
        };

        a = getAction(paramsStudent);
        r = getJsonResult(a);

        response = (MessageOutput) r.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        StudentAttributes student = logic.getStudentForEmail(student1OfCourse1.getCourse(), student1OfCourse1.getEmail());
        assertNotNull(student);
        assertEquals("", student.getGoogleId());
        assertNull(logic.getStudentForGoogleId(student1OfCourse1.getCourse(), student1OfCourse1.getGoogleId()));

        ______TS("typical success case: reset student account which has been already reset: failed silently");

        a = getAction(paramsStudent);
        r = getJsonResult(a);

        response = (MessageOutput) r.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        student = logic.getStudentForEmail(student1OfCourse1.getCourse(), student1OfCourse1.getEmail());
        assertNotNull(student);
        assertEquals("", student.getGoogleId());
        assertNull(logic.getStudentForGoogleId(student1OfCourse1.getCourse(), student1OfCourse1.getGoogleId()));
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
