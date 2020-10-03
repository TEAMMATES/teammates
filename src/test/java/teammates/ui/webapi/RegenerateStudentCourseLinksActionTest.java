package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateStudentCourseLinksData;

/**
 * SUT: {@link RegenerateStudentCourseLinksAction}.
 */
public class RegenerateStudentCourseLinksActionTest extends BaseActionTest<RegenerateStudentCourseLinksAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_COURSE_LINKS_REGENERATION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    public void baseClassSetup() {
        loginAsAdmin();
    }

    @Test
    protected void testExecute_notEnoughParameters() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        RegenerateStudentCourseLinksAction a = getAction(nonExistingParams);
        JsonResult result = getJsonResult(a);

        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(String.format(RegenerateStudentCourseLinksAction.STUDENT_NOT_FOUND,
                                student1InCourse1.email, "non-existent-course"),
                     output.getMessage());
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
    }

    @Test
    protected void testExecute_nonExistentStudentInCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("student with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@abc.com",
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
        };

        assertNull(logic.getStudentForEmail(student1InCourse1.course, "non-existent-student@abc.com"));

        RegenerateStudentCourseLinksAction a = getAction(nonExistingParams);
        JsonResult result = getJsonResult(a);

        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(String.format(RegenerateStudentCourseLinksAction.STUDENT_NOT_FOUND,
                                "non-existent-student@abc.com", student1InCourse1.course),
                     output.getMessage());
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
    }

    @Test
    protected void testExecute_regenerateCourseStudent() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        ______TS("Successfully sent regenerated links email");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
                Const.ParamsNames.COURSE_ID, student1InCourse1.course,
        };

        RegenerateStudentCourseLinksAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        RegenerateStudentCourseLinksData output = (RegenerateStudentCourseLinksData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals(RegenerateStudentCourseLinksAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
        assertNotEquals(student1InCourse1.key, output.getNewRegistrationKey());

        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(),
                                    typicalBundle.courses.get("typicalCourse1").getName(), student1InCourse1.course),
                     emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
