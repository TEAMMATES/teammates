package teammates.logic.external;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.apache.http.HttpStatus;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.jsoup.Jsoup;
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
        wrapper.setSenderName("Sender Name");
        wrapper.setSenderEmail("sender@exaple.com");
        wrapper.setReplyTo("replyto@example.com");
        wrapper.setRecipient("recipient@example.com");
        wrapper.setBcc("bcc@example.com");
        wrapper.setSubject("Test subject");
        wrapper.setContent("<p>This is a test content</p>");
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
    public void testSendEmail_smtpError5xx_throwsBadRequest() {
        for (int code : new int[] { 500, 550, 554 }) {
            SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(code));
            EmailSendingException e = assertThrows(EmailSendingException.class,
                    () -> service.sendEmail(buildMinimalWrapper()));
            assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testSendEmail_smtpError4xx_throwsBadGateway() {
        for (int code : new int[] { 421, 450, 451, 452 }) {
            SmtpServiceStub service = new SmtpServiceStub(buildSmtpException(code));
            EmailSendingException e = assertThrows(EmailSendingException.class,
                    () -> service.sendEmail(buildMinimalWrapper()));
            assertEquals(HttpStatus.SC_BAD_GATEWAY, e.getStatusCode());
        }
    }

    @Test
    public void testSendEmail_messagingException_throwsInternalServerError() {
        SmtpServiceStub service = new SmtpServiceStub(new MessagingException("Connection refused"));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.sendEmail(buildMinimalWrapper()));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getStatusCode());
    }

    @Test
    public void testConstructor_validConfigs_constructsSuccessfully() {
        assertDoesNotThrow(() -> {
            new SmtpService("smtp.example.invalid", "587", "starttls",
                    "true", "username", "password");
        });
        assertDoesNotThrow(() -> {
            new SmtpService("smtp.example.invalid", "587", "ssl",
                    "false", "", "");
        });
    }

    @Test
    public void testConstructor_invalidSecurityProtocol_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", null, "true",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "", "true",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "invalid_protocol", "true",
                    "username", "password");
        });
    }

    @Test
    public void testSmtpService_invalidAuthEnabled_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", null, "",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "ssl", null,
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "starttls", "invalid_value",
                    "username", "password");
        });
    }

    @Test
    public void testSmtpService_authEnabledWithMissingCredentials_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", null, "true",
                    "username", "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "ssl", "true",
                    null, "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpService("smtp.example.invalid", "587", "starttls", "true",
                    "", "");
        });
    }

    @Test
    public void testParseToEmail_returnsValidMessage() throws Exception {
        EmailWrapper wrapper = buildMinimalWrapper();
        SmtpService smtpService = new SmtpService("smtp.example.invalid", "587", "starttls", "false",
                "", "");
        MimeMessage email = smtpService.parseToEmail(wrapper);

        // Verify sender, recipient, reply-to and subject
        InternetAddress fromAddress = (InternetAddress) email.getFrom()[0];
        assertEquals(wrapper.getSenderEmail(), fromAddress.getAddress());
        assertEquals(wrapper.getSenderName(), fromAddress.getPersonal());
        assertEquals(wrapper.getRecipient(), email.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(wrapper.getBcc(), email.getRecipients(MimeMessage.RecipientType.BCC)[0].toString());
        assertEquals(wrapper.getReplyTo(), email.getReplyTo()[0].toString());
        assertEquals(wrapper.getSubject(), email.getSubject());

        // Verify HTML and text part of email content
        MimeMultipart multipart = (MimeMultipart) email.getContent();
        assertEquals(2, multipart.getCount());
        MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
        assertEquals(Jsoup.parse(wrapper.getContent()).text(), textPart.getContent().toString());
        MimeBodyPart htmlPart = (MimeBodyPart) multipart.getBodyPart(1);
        assertEquals(wrapper.getContent(), htmlPart.getContent().toString());
    }

    /**
     * A subclass that overrides {@link SmtpService#sendMessageWithTransport} to mock SMTP transport sending behaviour.
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
