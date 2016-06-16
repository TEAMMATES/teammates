package teammates.logic.core;

import org.jsoup.Jsoup;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

public class SendgridService implements EmailSenderService {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Email parseToEmail(EmailWrapper wrapper) {
        Email email = new Email();
        email.setFrom(wrapper.getSenderEmail());
        email.setFromName(wrapper.getSenderName());
        email.setReplyTo(wrapper.getReplyTo());
        for (String recipient : wrapper.getRecipientsList()) {
            email.addTo(recipient);
        }
        for (String bcc : wrapper.getBccList()) {
            email.addBcc(bcc);
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
    public void sendEmail(EmailWrapper wrapper) throws SendGridException {
        Email email = parseToEmail(wrapper);
        SendGrid sendgrid = new SendGrid(Config.SENDGRID_APIKEY);
        Response response = sendgrid.send(email);
        if (response.getCode() != SUCCESS_CODE) {
            log.severe("Email failed to send: " + response.getMessage());
        }
    }
    
}
