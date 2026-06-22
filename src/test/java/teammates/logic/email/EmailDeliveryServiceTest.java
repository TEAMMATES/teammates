package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import teammates.logic.external.email.EmptyEmailTransport;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailDeliveryService}.
 */
public class EmailDeliveryServiceTest extends BaseTestCase {

    private EmailWrapper getEmailToTestDomain() {
        EmailWrapper message = new EmailWrapper();
        message.setRecipient("student" + Const.TEST_EMAIL_DOMAIN);
        message.setSubject("Test subject");
        message.setContent("<p>Test content</p>");
        return message;
    }

    @Test
    public void testDeliver_allowEmailsToTestDomainFalse_blocksTestRecipient() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            mockConfig.when(Config::isAllowSendingEmailsToTestDomain).thenReturn(false);
            EmptyEmailTransport emptyEmailTransport = new EmptyEmailTransport();
            EmailDeliveryService emailDeliveryService = new EmailDeliveryService(emptyEmailTransport);
            EmailSendingStatus status = emailDeliveryService.deliver(getEmailToTestDomain());
            assertEquals(HttpStatus.SC_OK, status.getStatusCode());
            assertEquals("Not sending email to test account", status.getMessage());
        }
    }

    @Test
    public void testDeliver_allowEmailsToTestDomainTrue_doesNotBlockTestRecipient() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            mockConfig.when(Config::isAllowSendingEmailsToTestDomain).thenReturn(true);
            EmptyEmailTransport emptyEmailTransport = new EmptyEmailTransport();
            EmailDeliveryService emailDeliveryService = new EmailDeliveryService(emptyEmailTransport);
            EmailSendingStatus status = emailDeliveryService.deliver(getEmailToTestDomain());
            assertTrue(status.isSuccess());
            assertNotEquals("Not sending email to test account", status.getMessage());
        }
    }
}
