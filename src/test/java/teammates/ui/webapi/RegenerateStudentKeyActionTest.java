package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.RegenerateKeyData;

/**
 * SUT: {@link RegenerateStudentKeyAction}.
 */
public class RegenerateStudentKeyActionTest extends BaseActionTest<RegenerateStudentKeyAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_KEY;
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
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentStudentInCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("student with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@abc.com",
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        assertNull(logic.getStudentForEmail(student1InCourse1.getCourse(), "non-existent-student@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_regenerateStudentKey() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        ______TS("Successfully sent regenerated links email");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        RegenerateStudentKeyAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        RegenerateKeyData output = (RegenerateKeyData) result.getOutput();

        assertEquals(RegenerateStudentKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
        assertNotEquals(student1InCourse1.getKey(), output.getNewRegistrationKey());

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(),
                                    typicalBundle.courses.get("typicalCourse1").getName(), student1InCourse1.getCourse()),
                     emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
