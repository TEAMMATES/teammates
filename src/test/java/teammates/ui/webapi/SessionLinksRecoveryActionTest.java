package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * SUT: {@link SessionLinksRecoveryAction}.
 */
public class SessionLinksRecoveryActionTest extends BaseActionTest<SessionLinksRecoveryAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LINKS_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    protected void testExecute_notEnoughParameters() {
        ______TS("Invalid parameters");
        // no params
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_invalidEmail_shouldFail() {
        ______TS("email address is not valid");
        String[] invalidEmailParam = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "invalid-email-address",
        };

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(invalidEmailParam);
        assertEquals("Invalid email address: invalid-email-address", ihpe.getMessage());
    }

    @Test
    protected void testExecute_nonExistingEmail() {
        ______TS("Typical case: non-existent email address");

        String[] nonExistingParam = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent@abc.com",
        };

        SessionLinksRecoveryAction a = getAction(nonExistingParam);
        JsonResult result = getJsonResult(a);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to "
                + "the specified email address: non-existent@abc.com", output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals("non-existent@abc.com", emailSent.getRecipient());
    }

    @Test
    protected void testExecute_noFeedbackSessionsFound() {
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        ______TS("Typical case: successfully sent recovery link email: No feedback sessions found");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse2.getEmail(),
        };

        SessionLinksRecoveryAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to the "
                        + "specified email address: " + student1InCourse2.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse2.getEmail(), emailSent.getRecipient());
    }

    @Test
    protected void testExecute_openedOrClosedAndUnpublishedSessions() {
        StudentAttributes student1InCourse3 = typicalBundle.students.get("student1InCourse3");
        ______TS("Typical case: successfully sent recovery link email: opened and unpublished, "
                + "closed and unpublished.");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse3.getEmail(),
        };

        SessionLinksRecoveryAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been "
                        + "sent to the specified email address: " + student1InCourse3.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse3.getEmail(), emailSent.getRecipient());
    }

    @Test
    protected void testExecute_openedOrClosedAndPublishedSessions() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        ______TS("Typical case: successfully sent recovery link email: opened and published, "
                + "closed and published.");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        SessionLinksRecoveryAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent "
                        + "to the specified email address: " + student1InCourse1.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
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
        verifyAnyUserCanAccess();
    }
}
