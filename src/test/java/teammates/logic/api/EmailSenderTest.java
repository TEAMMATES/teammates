package teammates.logic.api;

import static org.mockito.Mockito.mockStatic;

import org.apache.http.HttpStatus;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailSender}.
 */
public class EmailSenderTest extends BaseTestCase {

    private EmailWrapper getEmailToTestDomain() {
        EmailWrapper message = new EmailWrapper();
        message.setRecipient("student" + Const.TEST_EMAIL_DOMAIN);
        message.setSubject("Test subject");
        message.setContent("<p>Test content</p>");
        return message;
    }

    @Test
    public void testSendEmail_allowEmailsToTestDomainFalse_blocksTestRecipient() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            mockConfig.when(Config::isAllowEmailsToTestDomain).thenReturn(false);
            EmailSender emailSender = new EmailSender();
            EmailSendingStatus status = emailSender.sendEmail(getEmailToTestDomain());
            assertEquals(HttpStatus.SC_OK, status.getStatusCode());
            assertEquals("Not sending email to test account", status.getMessage());
        }
    }

    @Test
    public void testSendEmail_allowEmailsToTestDomainTrue_doesNotBlockTestRecipient() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            mockConfig.when(Config::isAllowEmailsToTestDomain).thenReturn(true);
            EmailSender emailSender = new EmailSender();
            EmailSendingStatus status = emailSender.sendEmail(getEmailToTestDomain());
            assertTrue(status.isSuccess());
            assertNotEquals("Not sending email to test account", status.getMessage());
        }
    }
}
