package teammates.logic.core;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Email sender service provided by SendGrid.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid">https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid</a>
 * @see SendGrid
 */
public class SendgridService extends EmailSenderService {

    private static final Logger log = Logger.getLogger();

    /**
     * {@inheritDoc}
     */
    @Override
    public Mail parseToEmail(EmailWrapper wrapper) {
        Mail email = new Mail();
        Email sender;
        if (wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()) {
            sender = new Email(wrapper.getSenderEmail());
        } else {
            sender = new Email(wrapper.getSenderEmail(), wrapper.getSenderName());
        }
        email.setFrom(sender);
        email.setReplyTo(new Email(wrapper.getReplyTo()));
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(wrapper.getRecipient()));
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            personalization.addBcc(new Email(wrapper.getBcc()));
        }
        email.addPersonalization(personalization);
        email.setSubject(wrapper.getSubject());
        email.addContent(new Content("text/plain", Jsoup.parse(wrapper.getContent()).text()));
        email.addContent(new Content("text/html", wrapper.getContent()));
        return email;
    }

    @Override
    protected void sendEmailWithService(EmailWrapper wrapper) throws IOException {
        Mail email = parseToEmail(wrapper);
        SendGrid sendgrid = new SendGrid(Config.SENDGRID_APIKEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(email.build());
        Response response = sendgrid.api(request);
        if (isNotSuccessStatus(response.getStatusCode())) {
            log.severe("Email failed to send: " + response.getBody());
        }
    }

}
