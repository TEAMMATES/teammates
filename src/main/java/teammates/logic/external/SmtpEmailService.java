package teammates.logic.external;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Jakarta mail-based SMTP email sender service.
 *
 * @see <a href="https://jakarta.ee/specifications/mail/">https://jakarta.ee/specifications/mail/</a>
 */
public class SmtpEmailService implements EmailSenderService {

    private final Session session;

    public SmtpEmailService() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", Config.SMTP_HOST);
        props.put("mail.smtp.port", Config.SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.SMTP_USERNAME, Config.SMTP_PASSWORD);
            }
        };

        this.session = Session.getInstance(props, authenticator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessage parseToEmail(EmailWrapper wrapper) {
        try {
            MimeMessage message = new MimeMessage(session);

            if (wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()) {
                message.setFrom(new InternetAddress(wrapper.getSenderEmail()));
            } else {
                message.setFrom(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName()));
            }
            message.setReplyTo(new InternetAddress[] { new InternetAddress(wrapper.getReplyTo()) });
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(wrapper.getRecipient()));
            if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
                message.setRecipient(Message.RecipientType.BCC, new InternetAddress(wrapper.getBcc()));
            }

            message.setSubject(wrapper.getSubject());
            Multipart multipart = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(Jsoup.parse(wrapper.getContent()).text(), "UTF-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(wrapper.getContent(), "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            Logger.getLogger().severe("Failed to parse email", e);
        }

        return null;
    }

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException {
        try {
            MimeMessage message = parseToEmail(wrapper);
            if (message == null) {
                return new EmailSendingStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Failed to parse email");
            }
            Transport.send(message);
            return new EmailSendingStatus(HttpStatus.SC_OK, "Email sent successfully");
        } catch (MessagingException e) {
            throw new EmailSendingException(e, HttpStatus.SC_BAD_GATEWAY);
        }
    }
}
