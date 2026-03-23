package teammates.logic.api;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.testng.annotations.Test;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Email;
import com.sendgrid.helpers.mail.Mail;
import com.sun.jersey.multipart.FormDataMultiPart;

import teammates.common.util.EmailWrapper;
import teammates.logic.external.MailgunService;
import teammates.logic.external.MailjetService;
import teammates.logic.external.SendgridService;
import teammates.logic.external.SmtpService;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SendgridService},
 *      {@link MailgunService},
 *      {@link MailjetService},
 *      {@link SmtpService}.
 */
public class EmailSenderTest extends BaseTestCase {

    private EmailWrapper getTypicalEmailWrapper() {
        String senderName = "Sender Name";
        String senderEmail = "sender@email.com";
        String replyTo = "replyto@email.com";
        String recipient = "recipient@email.com";
        String bcc = "bcc@email.com";
        String subject = "Test subject";
        String content = "<p>This is a test content</p>";

        EmailWrapper wrapper = new EmailWrapper();
        wrapper.setSenderName(senderName);
        wrapper.setSenderEmail(senderEmail);
        wrapper.setReplyTo(replyTo);
        wrapper.setRecipient(recipient);
        wrapper.setBcc(bcc);
        wrapper.setSubject(subject);
        wrapper.setContent(content);
        return wrapper;
    }

    @Test
    public void testConvertToSendgrid() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        Mail email = new SendgridService().parseToEmail(wrapper);

        assertEquals(wrapper.getSenderEmail(), email.getFrom().getEmail());
        assertEquals(wrapper.getSenderName(), email.getFrom().getName());
        assertEquals(wrapper.getRecipient(), email.personalization.get(0).getTos().get(0).getEmail());
        assertEquals(wrapper.getBcc(), email.personalization.get(0).getBccs().get(0).getEmail());
        assertEquals(wrapper.getReplyTo(), email.getReplyto().getEmail());
        assertEquals(wrapper.getSubject(), email.getSubject());
        assertEquals("text/plain", email.getContent().get(0).getType());
        assertEquals(Jsoup.parse(wrapper.getContent()).text(), email.getContent().get(0).getValue());
        assertEquals("text/html", email.getContent().get(1).getType());
        assertEquals(wrapper.getContent(), email.getContent().get(1).getValue());
    }

    @Test
    public void testConvertToMailgun() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        try (FormDataMultiPart formData = new MailgunService().parseToEmail(wrapper)) {

            assertEquals(wrapper.getSenderName() + " <" + wrapper.getSenderEmail() + ">",
                    formData.getField("from").getValue());
            assertEquals(wrapper.getRecipient(), formData.getField("to").getValue());
            assertEquals(wrapper.getBcc(), formData.getField("bcc").getValue());
            assertEquals(wrapper.getReplyTo(), formData.getField("h:Reply-To").getValue());
            assertEquals(wrapper.getSubject(), formData.getField("subject").getValue());
            assertEquals(wrapper.getContent(), formData.getField("html").getValue());
        }
    }

    @Test
    public void testConvertToMailjet() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        MailjetRequest request = new MailjetService().parseToEmail(wrapper);
        JSONObject email = new JSONObject(request.getBody());

        assertEquals(wrapper.getSenderEmail(), email.get(Email.FROMEMAIL));
        assertEquals(wrapper.getSenderName(), email.get(Email.FROMNAME));
        assertEquals(wrapper.getRecipient(),
                     ((JSONArray) email.get(Email.RECIPIENTS)).getJSONObject(0).get("Email"));
        assertEquals(wrapper.getBcc(),
                     ((JSONArray) email.get(Email.RECIPIENTS)).getJSONObject(1).get("Email"));
        assertEquals(wrapper.getReplyTo(),
                     ((JSONObject) email.get(Email.HEADERS)).getString("Reply-To"));
        assertEquals(wrapper.getSubject(), email.get(Email.SUBJECT));
        assertEquals(wrapper.getContent(), email.get(Email.HTMLPART));
    }

    @Test
    public void testConvertToSmtp_authEnabled_success() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        SmtpService smtpService = new SmtpService("smtp.example.invalid", "587", "starttls", "true",
                "username", "password");
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

    @Test
    public void testConvertToSmtp_authDisabled_success() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
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

    @Test
    public void testSmtpSender_invalidSecurityProtocol_exceptionThrown() {
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
    public void testSmtpSender_invalidAuthEnabled_exceptionThrown() {
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
    public void testSmtpSender_authEnabledWithMissingCredentials_exceptionThrown() {
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
}
