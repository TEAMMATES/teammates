package teammates.logic.api;

import org.apache.http.HttpStatus;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.test.BaseTestCase;

/**
 * Unit test for {@link EmailSender}.
 */
public class EmailSenderUnitTest extends BaseTestCase {

    @Test
    public void testSendEmail_testDomain_blockedByDefault() {
        EmailSender emailSender = new EmailSender();
        EmailWrapper email = new EmailWrapper();
        email.setRecipient("test" + Const.TEST_EMAIL_DOMAIN);

        // By default it should be blocked
        EmailSendingStatus status = emailSender.sendEmail(email);
        assertEquals(HttpStatus.SC_OK, status.getStatusCode());
        assertEquals("Not sending email to test account", status.getMessage());
    }

    @Test
    public void testSendEmail_testDomain_allowedByConfig() {
        try (MockedStatic<Config> configMock = Mockito.mockStatic(Config.class)) {
            configMock.when(Config::isEmailSendingToTestDomainEnabled).thenReturn(true);
            configMock.when(Config::isUsingSendgrid).thenReturn(false);
            configMock.when(Config::isUsingMailgun).thenReturn(false);
            configMock.when(Config::isUsingMailjet).thenReturn(false);
            configMock.when(Config::isUsingSmtp).thenReturn(false);

            EmailSender emailSender = new EmailSender();
            EmailWrapper email = new EmailWrapper();
            email.setRecipient("test" + Const.TEST_EMAIL_DOMAIN);
            email.setSenderEmail("sender@email.com");
            email.setSubject("Subject");
            email.setContent("Content");

            // Should NOT be blocked now
            EmailSendingStatus status = emailSender.sendEmail(email);
            // It should proceed to the service, which is EmptyEmailService (returns success but does nothing)
            assertEquals(HttpStatus.SC_OK, status.getStatusCode());
            assertNull(status.getMessage());
        }
    }
}
