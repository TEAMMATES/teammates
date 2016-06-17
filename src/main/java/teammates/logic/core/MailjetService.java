package teammates.logic.core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Email;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

public class MailjetService implements EmailSenderService {
    
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
        
        JSONArray recipients = new JSONArray();
        for (String recipient : wrapper.getRecipientsList()) {
            recipients.put(new JSONObject().put("Email", recipient));
        }
        request.property(Email.RECIPIENTS, recipients);
        
        if (!wrapper.getBccList().isEmpty()) {
            JSONArray bccs = new JSONArray();
            for (String bcc : wrapper.getBccList()) {
                recipients.put(new JSONObject().put("Email", bcc));
            }
            request.property(Email.BCC, bccs);
        }
        
        request.property(Email.HEADERS, new JSONObject().put("Reply-To", wrapper.getReplyTo()));
        request.property(Email.SUBJECT, wrapper.getSubject());
        request.property(Email.HTMLPART, wrapper.getContent());
        request.property(Email.TEXTPART, Jsoup.parse(wrapper.getContent()).text());
        return request;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(EmailWrapper wrapper) throws MailjetException {
        MailjetRequest email = parseToEmail(wrapper);
        MailjetClient mailjet = new MailjetClient(Config.MAILJET_APIKEY, Config.MAILJET_SECRETKEY);
        MailjetResponse response = mailjet.post(email);
        if (response.getStatus() != SUCCESS_CODE) {
            log.severe("Email failed to send: " + response.getData().toString());
        }
    }
    
}
