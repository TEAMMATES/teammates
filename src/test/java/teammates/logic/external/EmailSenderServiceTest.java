package teammates.logic.external;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Email;
import com.sendgrid.helpers.mail.Mail;

import teammates.common.util.EmailWrapper;
import teammates.common.util.HtmlHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SendgridService},
 *      {@link MailjetService}.
 */
public class EmailSenderServiceTest extends BaseTestCase {

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
        assertEquals(HtmlHelper.htmlToPlainText(wrapper.getContent()), email.getContent().get(0).getValue());
        assertEquals("text/html", email.getContent().get(1).getType());
        assertEquals(wrapper.getContent(), email.getContent().get(1).getValue());
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
        assertEquals(HtmlHelper.htmlToPlainText(wrapper.getContent()), email.get(Email.TEXTPART));
    }

}
