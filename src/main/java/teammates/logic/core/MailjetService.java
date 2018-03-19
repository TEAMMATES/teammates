package teammates.logic.core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Email sender service provided by Mailjet.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/mail/mailjet">https://cloud.google.com/appengine/docs/java/mail/mailjet</a>
 * @see MailjetClient
 * @see MailjetRequest
 * @see MailjetResponse
 */
public class MailjetService extends EmailSenderService {

    private static final Logger log = Logger.getLogger();

    /**
     * {@inheritDoc}
     */
    @Override
    public MailjetRequest parseToEmail(EmailWrapper wrapper) {
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
        request.property(Email.TEXTPART, Jsoup.parse(wrapper.getContent()).text());
        return request;
    }

    @Override
    protected void sendEmailWithService(EmailWrapper wrapper) throws MailjetException, MailjetSocketTimeoutException {
        MailjetRequest email = parseToEmail(wrapper);
        MailjetClient mailjet = new MailjetClient(Config.MAILJET_APIKEY, Config.MAILJET_SECRETKEY);
        MailjetResponse response = mailjet.post(email);
        if (isNotSuccessStatus(response.getStatus())) {
            log.severe("Email failed to send: " + response.getData().toString());
        }
    }

}
