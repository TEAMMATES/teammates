package teammates.sqllogic.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SqlEmailGenerator}.
 */
public class SqlEmailGeneratorTest extends BaseTestCase {
    private final SqlEmailGenerator sqlEmailGenerator = SqlEmailGenerator.inst();

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_typicalCase_generatesSuccessfully() {
        AccountRequest accountRequest = new AccountRequest("chosen-one@jedi.org", "Anakin Skywalker", "Jedi Order",
                AccountRequestStatus.PENDING,
                "I don't like sand. It's coarse and rough and irritating... and it gets everywhere.");
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL);
    }

    private void verifyEmail(EmailWrapper email, String expectedRecipientEmailAddress) {
        assertEquals(expectedRecipientEmailAddress, email.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
    }
}
