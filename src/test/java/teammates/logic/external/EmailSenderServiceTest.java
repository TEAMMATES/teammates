package teammates.logic.external;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals(wrapper.getSenderEmail(), email.getFrom().getEmail());
        Assertions.assertEquals(wrapper.getSenderName(), email.getFrom().getName());
        Assertions.assertEquals(wrapper.getRecipient(), email.personalization.get(0).getTos().get(0).getEmail());
        Assertions.assertEquals(wrapper.getBcc(), email.personalization.get(0).getBccs().get(0).getEmail());
        Assertions.assertEquals(wrapper.getReplyTo(), email.getReplyto().getEmail());
        Assertions.assertEquals(wrapper.getSubject(), email.getSubject());
        Assertions.assertEquals("text/plain", email.getContent().get(0).getType());
        Assertions.assertEquals(HtmlHelper.htmlToPlainText(wrapper.getContent()), email.getContent().get(0).getValue());
        Assertions.assertEquals("text/html", email.getContent().get(1).getType());
        Assertions.assertEquals(wrapper.getContent(), email.getContent().get(1).getValue());
    }

    @Test
    public void testConvertToMailjet() {
        EmailWrapper wrapper = getTypicalEmailWrapper();
        MailjetRequest request = new MailjetService().parseToEmail(wrapper);
        JSONObject email = new JSONObject(request.getBody());

        Assertions.assertEquals(wrapper.getSenderEmail(), email.get(Email.FROMEMAIL));
        Assertions.assertEquals(wrapper.getSenderName(), email.get(Email.FROMNAME));
        Assertions.assertEquals(wrapper.getRecipient(),
                     ((JSONArray) email.get(Email.RECIPIENTS)).getJSONObject(0).get("Email"));
        Assertions.assertEquals(wrapper.getBcc(),
                     ((JSONArray) email.get(Email.RECIPIENTS)).getJSONObject(1).get("Email"));
        Assertions.assertEquals(wrapper.getReplyTo(),
                     ((JSONObject) email.get(Email.HEADERS)).getString("Reply-To"));
        Assertions.assertEquals(wrapper.getSubject(), email.get(Email.SUBJECT));
        Assertions.assertEquals(wrapper.getContent(), email.get(Email.HTMLPART));
        Assertions.assertEquals(HtmlHelper.htmlToPlainText(wrapper.getContent()), email.get(Email.TEXTPART));
    }

}
