package teammates.it.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.SessionLinksRecoveryResponseData;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SessionLinksRecoveryAction;

/**
 * SUT: {@link SessionLinksRecoveryActionTestIT}.
 */
public class SessionLinksRecoveryActionTestIT extends BaseActionIT<SessionLinksRecoveryAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.SESSION_LINKS_RECOVERY;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

        ______TS("Not enough parameters");
        // no params
        verifyHttpParameterFailure();

        ______TS("Failure: email address is not valid");
        String[] invalidEmailParam = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "invalid-email-address",
        };

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(invalidEmailParam);
        assertEquals("Invalid email address: invalid-email-address", ihpe.getMessage());

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

        EmailWrapper emailSent = getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals("non-existent@abc.com", emailSent.getRecipient());

        ______TS("Typical case: successfully sent recovery link email: No feedback sessions found");
        Student student1InCourse2 = typicalBundle.students.get("student1InCourse2");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse2.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent to the "
                        + "specified email address: " + student1InCourse2.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        emailSent = getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse2.getEmail(), emailSent.getRecipient());

        ______TS("Typical case test 1: successfully sent recovery link email: opened session and unpublished feedback, "
                + "closed session and unpublished feedback.");
        Student student1InCourse3 = typicalBundle.students.get("student1InCourse3");

        param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse3.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been "
                        + "sent to the specified email address: " + student1InCourse3.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        emailSent = getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse3.getEmail(), emailSent.getRecipient());

        ______TS("Typical case test 2: successfully sent recovery link email: opened and published, "
                + "closed and published.");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        a = getAction(param);
        result = getJsonResult(a);

        output = (SessionLinksRecoveryResponseData) result.getOutput();

        assertEquals("The recovery links for your feedback sessions have been sent "
                        + "to the specified email address: " + student1InCourse1.getEmail(),
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        emailSent = getEmailsSent().get(0);
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), emailSent.getSubject());
        assertEquals(student1InCourse1.getEmail(), emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }
}
