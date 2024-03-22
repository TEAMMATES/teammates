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
                "TEAMMATES: New Account Request Received", "/adminNewAccountRequestAlertEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT,
                "TEAMMATES: New Account Request Received", "/adminNewAccountRequestAlertEmailWithNoComments.html");
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

    private void verifyEmailContentHasNoPlaceholders(String emailContent) {
        assertFalse(emailContent.contains("${"));
    }
}
