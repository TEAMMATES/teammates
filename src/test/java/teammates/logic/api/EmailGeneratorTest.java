package teammates.logic.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link EmailGenerator}.
 */
public class EmailGeneratorTest extends BaseTestCase {
    private final EmailGenerator emailGenerator = EmailGenerator.inst();

    @Test
    void testGenerateNewAccountVerificationRequestAdminAlertEmail_withComments_generatesSuccessfully() throws IOException {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest(
                "chosen-one@jedi.org", "Anakin Skywalker", AccountVerificationRequestStatus.PENDING,
                "I don't like sand. It's coarse and rough and irritating... and it gets everywhere.");
        new Institute("Jedi Order", "SG").addAccountVerificationRequest(accountVerificationRequest);
        EmailWrapper email =
                emailGenerator.generateNewAccountVerificationRequestAdminAlertEmail(accountVerificationRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Verification Request Received",
                "/adminNewAccountVerificationRequestAlertEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountVerificationRequestAdminAlertEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest("maul@sith.org", "Maul",
                AccountVerificationRequestStatus.PENDING, null);
        new Institute("Sith Order", "SG").addAccountVerificationRequest(accountVerificationRequest);
        EmailWrapper email =
                emailGenerator.generateNewAccountVerificationRequestAdminAlertEmail(accountVerificationRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Verification Request Received",
                "/adminNewAccountVerificationRequestAlertEmailWithNoComments.html");
    }

    @Test
    void testGenerateNewAccountVerificationRequestAcknowledgementEmail_withComments_generatesSuccessfully()
            throws IOException {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest(
                "darth-vader@sith.org", "Darth Vader", AccountVerificationRequestStatus.PENDING,
                "I Am Your Father");
        new Institute("Sith Order", "SG").addAccountVerificationRequest(accountVerificationRequest);
        EmailWrapper email =
                emailGenerator.generateNewAccountVerificationRequestAcknowledgementEmail(accountVerificationRequest);
        verifyEmail(email, "darth-vader@sith.org", EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Verification Request",
                "/instructorNewAccountVerificationRequestAcknowledgementEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountVerificationRequestAcknowledgementEmail_withNoComments_generatesSuccessfully()
            throws IOException {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest("maul@sith.org", "Maul",
                AccountVerificationRequestStatus.PENDING, null);
        new Institute("Sith Order", "SG").addAccountVerificationRequest(accountVerificationRequest);
        EmailWrapper email =
                emailGenerator.generateNewAccountVerificationRequestAcknowledgementEmail(accountVerificationRequest);
        verifyEmail(email, "maul@sith.org", EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Verification Request",
                "/instructorNewAccountVerificationRequestAcknowledgementEmailWithNoComments.html");
    }

    @Test
    void testGenerateAccountVerificationRequestRejectionEmail_withDefaultReason_generatesSuccessfully() throws IOException {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest("maul@sith.org", "Maul",
                AccountVerificationRequestStatus.PENDING, null);
        new Institute("Sith Order", "SG").addAccountVerificationRequest(accountVerificationRequest);
        String title = "We are Unable to Create an Account for you";
        String content = new StringBuilder()
                            .append("<p>Hi, Maul</p>\n")
                            .append("<p>Thanks for your interest in using TEAMMATES. ")
                            .append("We are unable to create a TEAMMATES instructor account for you.</p>\n\n")
                            .append("<p>\n")
                            .append("  <strong>Reason:</strong> The email address you provided ")
                            .append("is not an 'official' email address provided by your institution.<br />\n")
                            .append("  <strong>Remedy:</strong> ")
                            .append("Please re-submit an account verification request with your "
                                    + "'official' institution email address.\n")
                            .append("</p>\n\n")
                            .append("<p>If you need further clarification or would like to appeal this decision, ")
                            .append("please feel free to contact us at teammates@comp.nus.edu.sg.</p>\n")
                            .append("<p>Regards,<br />TEAMMATES Team.</p>\n")
                            .toString();

        EmailWrapper email = emailGenerator.generateAccountVerificationRequestRejectionEmail(
                accountVerificationRequest, title, content);
        verifyEmail(email, "maul@sith.org", EmailType.ACCOUNT_VERIFICATION_REQUEST_REJECTION,
                "TEAMMATES: " + title,
                Config.SUPPORT_EMAIL,
                "/instructorAccountVerificationRequestRejectionEmail.html");
    }

    private void verifyEmail(EmailWrapper email, String expectedRecipientEmailAddress, EmailType expectedEmailType,
            String expectedSubject, String expectedEmailContentFilePathname) throws IOException {
        assertEquals(expectedRecipientEmailAddress, email.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        assertEquals(expectedEmailType, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        String emailContent = email.getContent();
        EmailChecker.verifyEmailContent(emailContent, expectedEmailContentFilePathname);
        verifyEmailContentHasNoPlaceholders(emailContent);
    }

    private void verifyEmail(EmailWrapper email, String expectedRecipientEmailAddress, EmailType expectedEmailType,
            String expectedSubject, String expectedBcc, String expectedEmailContentFilePathname) throws IOException {
        assertEquals(expectedRecipientEmailAddress, email.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        assertEquals(expectedEmailType, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertEquals(expectedBcc, email.getBcc());
        String emailContent = email.getContent();
        EmailChecker.verifyEmailContent(emailContent, expectedEmailContentFilePathname);
        verifyEmailContentHasNoPlaceholders(emailContent);
    }

    private void verifyEmailContentHasNoPlaceholders(String emailContent) {
        assertFalse(emailContent.contains("${"));
    }
}
