package teammates.logic.core;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import teammates.common.util.Config;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Email sender service provided by SendGrid.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid">https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-sendgrid</a>
 * @see SendGrid
 */
public class SendgridService extends EmailSenderService {

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
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) throws IOException {
        Mail email = parseToEmail(wrapper);
        SendGrid sendgrid = new SendGrid(Config.SENDGRID_APIKEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(email.build());
        Response response = sendgrid.api(request);
        return new EmailSendingStatus(response.getStatusCode(), response.getBody());
    }

}
