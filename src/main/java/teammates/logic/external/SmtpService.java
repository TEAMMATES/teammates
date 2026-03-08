package teammates.logic.external;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.SendFailedException;
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

/**
 * Jakarta mail-based SMTP email sender service.
 *
 * @see <a href="https://javadoc.io/doc/com.sun.mail/jakarta.mail/2.0.1/jakarta.mail/com/sun/mail/smtp/package-summary.html">
 *          SMTP-related Session properties</a>
 */
public class SmtpService implements EmailSenderService {
    private static final String DEFAULT_CONNECTION_TIMEOUT = "10000";
    private static final String TEXT_ENCODING_UTF8 = "UTF-8";
    private final Session session;

    public SmtpService() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", Config.SMTP_HOST);
        props.put("mail.smtp.port", Config.SMTP_PORT);
        props.put("mail.smtp.auth", "true");

        // Set security protocol
        boolean isUsingSsl = "ssl".equalsIgnoreCase(Config.SMTP_SECURITY_PROTOCOL);
        boolean isUsingStartTls = "starttls".equalsIgnoreCase(Config.SMTP_SECURITY_PROTOCOL);
        if (!isUsingSsl && !isUsingStartTls) {
            throw new IllegalArgumentException("Unsupported SMTP security protocol: " + Config.SMTP_SECURITY_PROTOCOL);
        }
        props.put("mail.smtp.ssl.enable", String.valueOf(isUsingSsl));
        props.put("mail.smtp.starttls.enable", String.valueOf(isUsingStartTls));
        props.put("mail.smtp.starttls.required", String.valueOf(isUsingStartTls));

        // Set default timeouts (in milliseconds) for SMTP socket connection, read, and write timeouts
        props.put("mail.smtp.connectiontimeout", DEFAULT_CONNECTION_TIMEOUT);
        props.put("mail.smtp.timeout", DEFAULT_CONNECTION_TIMEOUT);
        props.put("mail.smtp.writetimeout", DEFAULT_CONNECTION_TIMEOUT);

        // Override default timeouts with values from config if provided
        String socketConnectionTimeout = Config.SMTP_SOCKET_CONNECTION_TIMEOUT;
        String socketReadTimeout = Config.SMTP_SOCKET_READ_TIMEOUT;
        String socketWriteTimeout = Config.SMTP_SOCKET_WRITE_TIMEOUT;
        setIfPresent(props, "mail.smtp.connectiontimeout", socketConnectionTimeout);
        setIfPresent(props, "mail.smtp.timeout", socketReadTimeout);
        setIfPresent(props, "mail.smtp.writetimeout", socketWriteTimeout);

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
            return createMimeMessage(wrapper);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException {
        try {
            MimeMessage message = createMimeMessage(wrapper);
            Transport.send(message);
            return new EmailSendingStatus(HttpStatus.SC_OK, "Email sent successfully");
        } catch (SendFailedException sfe) {
            if (sfe.getInvalidAddresses() != null && sfe.getInvalidAddresses().length > 0) {
                throw new EmailSendingException(sfe, HttpStatus.SC_BAD_REQUEST);
            }
            throw new EmailSendingException(sfe, HttpStatus.SC_BAD_GATEWAY);
        } catch (MessagingException me) {
            throw new EmailSendingException(me, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (UnsupportedEncodingException uee) {
            throw new EmailSendingException(uee, HttpStatus.SC_BAD_REQUEST);
        }
    }

    private MimeMessage createMimeMessage(EmailWrapper wrapper) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);

        // Set email sender, recipient, reply-to, and subject
        if (wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()) {
            message.setFrom(new InternetAddress(wrapper.getSenderEmail()));
        } else {
            message.setFrom(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName(), TEXT_ENCODING_UTF8));
        }
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(wrapper.getRecipient()));
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            message.setRecipient(Message.RecipientType.BCC, new InternetAddress(wrapper.getBcc()));
        }
        message.setReplyTo(new InternetAddress[] { new InternetAddress(wrapper.getReplyTo()) });
        message.setSubject(wrapper.getSubject(), TEXT_ENCODING_UTF8);

        // Set email content in text and HTML part
        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(Jsoup.parse(wrapper.getContent()).text(), TEXT_ENCODING_UTF8);
        multipart.addBodyPart(textPart);
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(wrapper.getContent(), String.format("text/html; charset=%s", TEXT_ENCODING_UTF8));
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        return message;
    }

    private void setIfPresent(Properties props, String key, String value) {
        if (value != null && !value.isEmpty()) {
            props.put(key, value);
        }
    }
}
