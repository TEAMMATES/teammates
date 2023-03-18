package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.ResetAccountAction;

/**
 * SUT: {@link ResetAccountAction}.
 */
public class ResetAccountActionIT extends BaseActionIT<ResetAccountAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student = typicalBundle.students.get("student1InCourse1");

        loginAsAdmin();

        ______TS("Typical Success Case with Student Email param given and Student exists");
        String[] params = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        ResetAccountAction resetAccountAction = getAction(params);
        JsonResult actionOutput = getJsonResult(resetAccountAction);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        assertNotNull(student);
        assertNull(student.getAccount());
        assertNull(student.getGoogleId());

        ______TS("Student Email param given but Student is non existent");
        String invalidEmail = "does-not-exist-email@teammates.tmt";
        String[] invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail,
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidParams);
        assertEquals("Student does not exist.", enfe.getMessage());

        ______TS("Typical Success Case with Instructor Email param given and Instructor exists");
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        resetAccountAction = getAction(params);
        actionOutput = getJsonResult(resetAccountAction);
        response = (MessageOutput) actionOutput.getOutput();

        assertEquals(response.getMessage(), "Account is successfully reset.");
        assertNotNull(instructor);
        assertNull(instructor.getAccount());
        assertNull(instructor.getGoogleId());

        ______TS("Instructor Email param given but Instructor is non existent");
        invalidParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        enfe = verifyEntityNotFound(invalidParams);
        assertEquals("Instructor does not exist.", enfe.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");

        verifyOnlyAdminCanAccess(course);
    }

}
