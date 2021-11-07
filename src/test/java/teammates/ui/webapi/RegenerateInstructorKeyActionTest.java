package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.RegenerateKeyData;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionTest extends BaseActionTest<RegenerateInstructorKeyAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_KEY;
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
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null instructor email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentInstructorInCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("instructor with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent-instructor@abc.com",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), "non-existent-instructor@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_regenerateInstructorKey() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        ______TS("Successfully sent regenerated links email");

        String[] param = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        RegenerateInstructorKeyAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        RegenerateKeyData output = (RegenerateKeyData) result.getOutput();

        assertEquals(RegenerateInstructorKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
        assertNotEquals(instructor1OfCourse1.getKey(), output.getNewRegistrationKey());

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                                   typicalBundle.courses.get("typicalCourse1").getName(),
                                   instructor1OfCourse1.getCourseId()),
                     emailSent.getSubject());
        assertEquals(instructor1OfCourse1.getEmail(), emailSent.getRecipient());
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
