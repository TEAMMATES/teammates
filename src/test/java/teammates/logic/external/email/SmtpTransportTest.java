package teammates.logic.external.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.apache.http.HttpStatus;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.testng.annotations.Test;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HtmlHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SmtpTransport}.
 */
public class SmtpTransportTest extends BaseTestCase {

    private static EmailWrapper getTypicalEmailWrapper() {
        EmailWrapper wrapper = new EmailWrapper();
        wrapper.setSenderName("Sender Name");
        wrapper.setSenderEmail("sender@example.com");
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
    public void testDeliver_noErrors_returnsOk() throws EmailSendingException {
        SmtpTransportStub service = new SmtpTransportStub(null);
        EmailSendingStatus status = service.deliver(getTypicalEmailWrapper());
        assertEquals(HttpStatus.SC_OK, status.getStatusCode());
    }

    @Test
    public void testDeliver_smtpError5xx_throwsBadRequest() {
        for (int code : new int[] { 500, 550, 554 }) {
            SmtpTransportStub service = new SmtpTransportStub(buildSmtpException(code));
            EmailSendingException e = assertThrows(EmailSendingException.class,
                    () -> service.deliver(getTypicalEmailWrapper()));
            assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testDeliver_smtpError4xx_throwsBadGateway() {
        for (int code : new int[] { 421, 450, 451, 452 }) {
            SmtpTransportStub service = new SmtpTransportStub(buildSmtpException(code));
            EmailSendingException e = assertThrows(EmailSendingException.class,
                    () -> service.deliver(getTypicalEmailWrapper()));
            assertEquals(HttpStatus.SC_BAD_GATEWAY, e.getStatusCode());
        }
    }

    @Test
    public void testDeliver_messagingException_throwsInternalServerError() {
        SmtpTransportStub service = new SmtpTransportStub(new MessagingException("Connection refused"));
        EmailSendingException e = assertThrows(EmailSendingException.class,
                () -> service.deliver(getTypicalEmailWrapper()));
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getStatusCode());
    }

    @Test
    public void testConstructor_validConfigs_constructsSuccessfully() {
        assertDoesNotThrow(() -> {
            new SmtpTransport("smtp.example.invalid", "587", "starttls",
                    "true", "username", "password");
        });
        assertDoesNotThrow(() -> {
            new SmtpTransport("smtp.example.invalid", "587", "ssl",
                    "false", "", "");
        });
    }

    @Test
    public void testConstructor_invalidSecurityProtocol_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", null, "true",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "", "true",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "invalid_protocol", "true",
                    "username", "password");
        });
    }

    @Test
    public void testSmtpTransport_invalidAuthEnabled_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", null, "",
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "ssl", null,
                    "username", "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "starttls", "invalid_value",
                    "username", "password");
        });
    }

    @Test
    public void testSmtpTransport_authEnabledWithMissingCredentials_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", null, "true",
                    "username", "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "ssl", "true",
                    null, "password");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SmtpTransport("smtp.example.invalid", "587", "starttls", "true",
                    "", "");
        });
    }

    @Test
    public void testParseToEmail_returnsValidMessage() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        SmtpTransport smtpService = new SmtpTransport("smtp.example.invalid", "587", "starttls", "false",
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
        assertEquals(HtmlHelper.htmlToPlainText(wrapper.getContent()), textPart.getContent().toString());
        MimeBodyPart htmlPart = (MimeBodyPart) multipart.getBodyPart(1);
        assertEquals(wrapper.getContent(), htmlPart.getContent().toString());
    }

    /**
     * A subclass that overrides {@link SmtpTransport#sendMessageWithTransport} to mock SMTP transport sending behaviour.
     */
    private static final class SmtpTransportStub extends SmtpTransport {

        private final MessagingException exceptionToThrow;

        SmtpTransportStub(MessagingException exceptionToThrow) {
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
