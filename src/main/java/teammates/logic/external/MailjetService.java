package teammates.logic.external;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Email;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HtmlHelper;

/**
 * Email sender service provided by Mailjet.
 */
public class MailjetService implements EmailSenderService {

    MailjetRequest parseToEmail(EmailWrapper wrapper) {
        MailjetRequest request = new MailjetRequest(Email.resource);
        request.property(Email.FROMEMAIL, wrapper.getSenderEmail());
        if (wrapper.getSenderName() != null && !wrapper.getSenderName().isEmpty()) {
            request.property(Email.FROMNAME, wrapper.getSenderName());
        }

        request.property(Email.RECIPIENTS, new JSONArray().put(new JSONObject().put("Email", wrapper.getRecipient())));
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            request.append(Email.RECIPIENTS, new JSONObject().put("Email", wrapper.getBcc()));
        }

        request.property(Email.HEADERS, new JSONObject().put("Reply-To", wrapper.getReplyTo()));
        request.property(Email.SUBJECT, wrapper.getSubject());
        request.property(Email.HTMLPART, wrapper.getContent());
        request.property(Email.TEXTPART, HtmlHelper.htmlToPlainText(wrapper.getContent()));
        return request;
    }

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException {
        MailjetRequest email = parseToEmail(wrapper);
        MailjetClient mailjet = new MailjetClient(
                ClientOptions.builder().apiKey(Config.MAILJET_APIKEY).apiSecretKey(Config.MAILJET_SECRETKEY).build());
        try {
            MailjetResponse response = mailjet.post(email);
            return new EmailSendingStatus(response.getStatus(), response.getData().toString());
        } catch (MailjetException e) {
            throw new EmailSendingException(e, HttpStatus.SC_BAD_GATEWAY);
        }
    }

}
