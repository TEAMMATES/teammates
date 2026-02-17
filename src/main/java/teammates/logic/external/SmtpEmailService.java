package teammates.logic.external;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

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

import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Jakarta mail-based SMTP email sender service.
 *
 * @see <a href="https://jakarta.ee/specifications/mail/">https://jakarta.ee/specifications/mail/</a>
 * @see <a href="https://javadoc.io/doc/com.sun.mail/jakarta.mail/2.0.1/jakarta.mail/com/sun/mail/smtp/package-summary.html">
 *          SMTP-related Session properties</a>
 */
public class SmtpEmailService implements EmailSenderService {
    private static final String DEFAULT_CONNECTION_TIMEOUT = "10000";
    private static final String EMAIL_TEXT_ENCODING = "UTF-8";
    private final Session session;

    public SmtpEmailService() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", Config.SMTP_HOST);
        props.put("mail.smtp.port", Config.SMTP_PORT);
        props.put("mail.smtp.auth", "true");

        boolean isUsingSsl = "ssl".equalsIgnoreCase(Config.SMTP_SECURITY_PROTOCOL);
        boolean isUsingStartTls = "starttls".equalsIgnoreCase(Config.SMTP_SECURITY_PROTOCOL);
        props.put("mail.smtp.ssl.enable", String.valueOf(isUsingSsl));
        props.put("mail.smtp.starttls.enable", String.valueOf(isUsingStartTls));

        // Set default timeouts (in milliseconds) for SMTP socket connection, read, and write timeouts
        props.put("mail.smtp.connectiontimeout", DEFAULT_CONNECTION_TIMEOUT);
        props.put("mail.smtp.timeout", DEFAULT_CONNECTION_TIMEOUT);
        props.put("mail.smtp.writetimeout", DEFAULT_CONNECTION_TIMEOUT);

        // Override default timeouts with values from config if provided
        String socketConnectionTimeout = Config.SMTP_CONNECTION_TIMEOUT;
        String socketConnectionReadTimeout = Config.SMTP_CONNECTION_READ_TIMEOUT;
        String socketConnectionWriteTimeout = Config.SMTP_CONNECTION_WRITE_TIMEOUT;
        if (socketConnectionTimeout != null && !socketConnectionTimeout.isEmpty()) {
            props.put("mail.smtp.connectiontimeout", socketConnectionTimeout);
        }
        if (socketConnectionReadTimeout != null && !socketConnectionReadTimeout.isEmpty()) {
            props.put("mail.smtp.timeout", socketConnectionReadTimeout);
        }
        if (socketConnectionWriteTimeout != null && !socketConnectionWriteTimeout.isEmpty()) {
            props.put("mail.smtp.writetimeout", socketConnectionWriteTimeout);
        }

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

            message.setSubject(wrapper.getSubject(), EMAIL_TEXT_ENCODING);
            Multipart multipart = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(Jsoup.parse(wrapper.getContent()).text(), EMAIL_TEXT_ENCODING);
            multipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(wrapper.getContent(), String.format("text/html; charset=%s", EMAIL_TEXT_ENCODING));
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
