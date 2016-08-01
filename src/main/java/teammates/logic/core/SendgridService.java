package teammates.logic.core;

import org.jsoup.Jsoup;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

/**
 * Email sender service provided by SendGrid.
 * Reference: https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid
 * 
 * @see SendGrid
 */
public class SendgridService extends EmailSenderService {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Email parseToEmail(EmailWrapper wrapper) {
        Email email = new Email();
        email.setFrom(wrapper.getSenderEmail());
        if (wrapper.getSenderName() != null && !wrapper.getSenderName().isEmpty()) {
            email.setFromName(wrapper.getSenderName());
        }
        email.setReplyTo(wrapper.getReplyTo());
        email.addTo(wrapper.getRecipient());
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            email.addBcc(wrapper.getBcc());
        }
        email.setSubject(wrapper.getSubject());
        email.setHtml(wrapper.getContent());
        email.setText(Jsoup.parse(wrapper.getContent()).text());
        return email;
    }
    
    @Override
    protected void sendEmailWithService(EmailWrapper wrapper) throws SendGridException {
        Email email = parseToEmail(wrapper);
        SendGrid sendgrid = new SendGrid(Config.SENDGRID_APIKEY);
        Response response = sendgrid.send(email);
        if (response.getCode() != SUCCESS_CODE) {
            log.severe("Email failed to send: " + response.getMessage());
        }
    }
    
}
