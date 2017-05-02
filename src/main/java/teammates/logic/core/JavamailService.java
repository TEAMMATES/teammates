package teammates.logic.core;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import teammates.common.util.EmailWrapper;

/**
 * Email sender service provided by JavaMail.
 * This is the default service provided by Google App Engine.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/java/mail/">https://cloud.google.com/appengine/docs/java/mail/</a>
 * @see MimeMessage
 */
public class JavamailService extends EmailSenderService {

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessage parseToEmail(EmailWrapper wrapper) throws AddressException, MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage email = new MimeMessage(session);
        if (wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()) {
            email.setFrom(new InternetAddress(wrapper.getSenderEmail()));
        } else {
            email.setFrom(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName()));
        }
        email.setReplyTo(new Address[] { new InternetAddress(wrapper.getReplyTo()) });
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(wrapper.getRecipient()));
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            email.addRecipient(Message.RecipientType.BCC, new InternetAddress(wrapper.getBcc()));
        }
        email.setSubject(wrapper.getSubject());
        email.setContent(wrapper.getContent(), "text/html");
        return email;
    }

    @Override
    protected void sendEmailWithService(EmailWrapper wrapper) throws AddressException, MessagingException, IOException {
        MimeMessage email = parseToEmail(wrapper);
        Transport.send(email);
    }

}
