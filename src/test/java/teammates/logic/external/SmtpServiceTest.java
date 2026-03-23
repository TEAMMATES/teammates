package teammates.logic.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.apache.http.HttpStatus;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.testng.annotations.Test;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SmtpService}.
 */
public class SmtpServiceTest extends BaseTestCase {

    private static EmailWrapper buildMinimalWrapper() {
        EmailWrapper wrapper = new EmailWrapper();
        wrapper.setSenderEmail("sender@example.com");
        wrapper.setRecipient("recipient@example.com");
        wrapper.setReplyTo("replyto@example.com");
        wrapper.setSubject("Test Subject");
        wrapper.setContent("<p>Test content</p>");
        return wrapper;
    }

    private static SMTPSendFailedException buildSmtpException(int returnCode) {
        return new SMTPSendFailedException("DATA", returnCode, "SMTP error", null, null, null, null);
    }

    @Test
    public void testSendEmail_noErrors_returnsOk() throws EmailSendingException {
        SmtpServiceStub service = new SmtpServiceStub(null);
        EmailSendingStatus status = service.sendEmail(buildMinimalWrapper());
        assertEquals(HttpStatus.SC_OK, status.getStatusCode());
    }

    @Test
    public void testSendEmail_smtpPermanentFailure_throwsBadRequest() {
        SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(550));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
    }

    @Test
    public void testSendEmail_smtpTransientFailure_throwsBadGateway() {
        SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(421));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_BAD_GATEWAY, e.getStatusCode());
    }

    @Test
    public void testSendEmail_smtp500_throwsBadRequest() {
        SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(500));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
    }

    @Test
    public void testSendEmail_smtp554_throwsBadRequest() {
        SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(554));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
    }

    @Test
    public void testSendEmail_messagingException_throwsInternalServerError() {
        SmtpServiceStub service = new SmtpServiceStub(new MessagingException("Connection refused"));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getStatusCode());
    }

    /**
     * A subclass that overrides {@link SmtpService#sendMessageWithTransport} to mock SMTP transport behaviour
     * without requiring a live SMTP connection.
     */
    private static final class SmtpServiceStub extends SmtpService {

        private final MessagingException exceptionToThrow;

        SmtpServiceStub(MessagingException exceptionToThrow) {
            super("localhost", "25", "ssl", "false", null, null);
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        protected void sendMessageWithTransport(MimeMessage message) throws MessagingException {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }
}