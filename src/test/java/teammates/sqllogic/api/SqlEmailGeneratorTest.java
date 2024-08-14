package teammates.sqllogic.api;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;

/**
 * SUT: {@link SqlEmailGenerator}.
 */
public class SqlEmailGeneratorTest extends BaseTestCase {
    private final SqlEmailGenerator sqlEmailGenerator = SqlEmailGenerator.inst();

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("chosen-one@jedi.org", "Anakin Skywalker", "Jedi Order",
                AccountRequestStatus.PENDING,
                "I don't like sand. It's coarse and rough and irritating... and it gets everywhere.");
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Request Received",
                "/adminNewAccountRequestAlertEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Request Received",
                "/adminNewAccountRequestAlertEmailWithNoComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_withComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("darth-vader@sith.org", "Darth Vader", "Sith Order",
                AccountRequestStatus.PENDING,
                "I Am Your Father");
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);
        verifyEmail(email, "darth-vader@sith.org", EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Request",
                "/instructorNewAccountRequestAcknowledgementEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);
        verifyEmail(email, "maul@sith.org", EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Request",
                "/instructorNewAccountRequestAcknowledgementEmailWithNoComments.html");
    }

    @Test
    void testGenerateAccountRequestRejectionEmail_withDefaultReason_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        String title = "We are Unable to Create an Account for you";
        String content = new StringBuilder()
                            .append("<p>Hi, Maul</p>\n")
                            .append("<p>Thanks for your interest in using TEAMMATES. ")
                            .append("We are unable to create a TEAMMATES instructor account for you.</p>\n\n")
                            .append("<p>\n")
                            .append("  <strong>Reason:</strong> The email address you provided ")
                            .append("is not an 'official' email address provided by your institution.<br />\n")
                            .append("  <strong>Remedy:</strong> ")
                            .append("Please re-submit an account request with your 'official' institution email address.\n")
                            .append("</p>\n\n")
                            .append("<p>If you need further clarification or would like to appeal this decision, ")
                            .append("please feel free to contact us at teammates@comp.nus.edu.sg.</p>\n")
                            .append("<p>Regards,<br />TEAMMATES Team.</p>\n")
                            .toString();

        EmailWrapper email = sqlEmailGenerator.generateAccountRequestRejectionEmail(accountRequest, title, content);
        verifyEmail(email, "maul@sith.org", EmailType.ACCOUNT_REQUEST_REJECTION,
                "TEAMMATES: " + title,
                Config.SUPPORT_EMAIL,
                "/instructorAccountRequestRejectionEmail.html");
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
