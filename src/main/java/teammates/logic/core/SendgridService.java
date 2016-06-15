package teammates.logic.core;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

public class SendgridService implements EmailSenderService {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Sendgrid parseToEmail(EmailWrapper wrapper) throws JSONException {
        Sendgrid email = new Sendgrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);
        email.setFrom(wrapper.getSenderEmail());
        email.setFromName(wrapper.getSenderName());
        email.setReplyTo(wrapper.getReplyTo());
        for (String recipient : wrapper.getRecipientsList()) {
            email.addTo(recipient);
        }
        if (!wrapper.getBccList().isEmpty()) {
            // Sendgrid does not support multiple BCCs
            email.setBcc(wrapper.getBccList().get(0));
        }
        email.setSubject(wrapper.getSubject());
        email.setHtml(wrapper.getContent());
        email.setText(Jsoup.parse(wrapper.getContent()).text());
        return email;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(EmailWrapper wrapper) throws JSONException, IOException {
        Sendgrid email = parseToEmail(wrapper);
        email.send();
    }
    
}
