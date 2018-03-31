package teammates.test.cases.logic;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.testng.annotations.Test;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Email;
import com.sendgrid.Mail;
import com.sun.jersey.multipart.FormDataMultiPart;

import teammates.common.util.EmailWrapper;
import teammates.logic.core.JavamailService;
import teammates.logic.core.MailgunService;
import teammates.logic.core.MailjetService;
import teammates.logic.core.SendgridService;

/**
 * SUT: {@link JavamailService},
 *      {@link SendgridService},
 *      {@link MailgunService},
 *      {@link MailjetService}.
 */
public class EmailSenderTest extends BaseLogicTest {

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

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
    public void testConvertToMimeMessage() throws Exception {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        MimeMessage email = new JavamailService().parseToEmail(wrapper);

        assertEquals(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName()), email.getFrom()[0]);
        assertEquals(new InternetAddress(wrapper.getReplyTo()), email.getReplyTo()[0]);
        assertEquals(new InternetAddress(wrapper.getRecipient()), email.getRecipients(Message.RecipientType.TO)[0]);
        assertEquals(new InternetAddress(wrapper.getBcc()), email.getRecipients(Message.RecipientType.BCC)[0]);
        assertEquals(wrapper.getSubject(), email.getSubject());
        assertEquals(wrapper.getContent(), email.getContent().toString());
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

}
