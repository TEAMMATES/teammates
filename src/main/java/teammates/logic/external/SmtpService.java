package teammates.logic.external;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
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
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HtmlHelper;
import teammates.common.util.StringHelper;

/**
 * Jakarta mail-based SMTP email sender service.
 *
 * @see <a href="https://javadoc.io/doc/com.sun.mail/jakarta.mail/2.0.1/jakarta.mail/com/sun/mail/smtp/package-summary.html">
 *          SMTP-related Session properties</a>
 */
public class SmtpService implements EmailSenderService {
    private static final String DEFAULT_TIMEOUT_MS = "10000";
    private static final String TEXT_ENCODING_UTF8 = "UTF-8";
    private final Session session;

    public SmtpService() {
        this(Config.SMTP_HOST, Config.SMTP_PORT, Config.SMTP_SECURITY_PROTOCOL,
                Config.SMTP_AUTH, Config.SMTP_USERNAME, Config.SMTP_PASSWORD);
    }

    public SmtpService(String host, String port, String securityProtocol, String authEnabled,
                       String username, String password) {
        boolean isAuthEnabled = Boolean.parseBoolean(authEnabled);
        boolean isAuthEnabledValid = "true".equalsIgnoreCase(authEnabled) || "false".equalsIgnoreCase(authEnabled);
        if (!isAuthEnabledValid) {
            throw new IllegalArgumentException("Invalid value for SMTP auth enabled: " + authEnabled);
        }
        boolean isCredentialsValid = !StringHelper.isEmpty(username) && !StringHelper.isEmpty(password);
        if (isAuthEnabled && !isCredentialsValid) {
            throw new IllegalArgumentException("SMTP auth is enabled but username or password is missing");
        }

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", authEnabled);

        // Set security protocol
        boolean isUsingSsl = "ssl".equalsIgnoreCase(securityProtocol);
        boolean isUsingStartTls = "starttls".equalsIgnoreCase(securityProtocol);
        if (!isUsingSsl && !isUsingStartTls) {
            throw new IllegalArgumentException("Unsupported SMTP security protocol: " + securityProtocol);
        }
        props.put("mail.smtp.ssl.enable", String.valueOf(isUsingSsl));
        props.put("mail.smtp.starttls.enable", String.valueOf(isUsingStartTls));
        props.put("mail.smtp.starttls.required", String.valueOf(isUsingStartTls));

        // Set default timeouts (in milliseconds) for SMTP socket connection, read, and write timeouts
        props.put("mail.smtp.connectiontimeout", DEFAULT_TIMEOUT_MS);
        props.put("mail.smtp.timeout", DEFAULT_TIMEOUT_MS);
        props.put("mail.smtp.writetimeout", DEFAULT_TIMEOUT_MS);

        if (isAuthEnabled) {
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            this.session = Session.getInstance(props, authenticator);
        } else {
            this.session = Session.getInstance(props);
        }
    }

    /**
     * Sends the given {@link MimeMessage} via SMTP transport.
     * Allows mocking of SMTP transport sending behaviour in tests.
     */
    protected void sendMessageWithTransport(MimeMessage message) throws MessagingException {
        Transport.send(message);
    }

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException {
        try {
            MimeMessage message = parseToEmail(wrapper);
            sendMessageWithTransport(message);
            return new EmailSendingStatus(HttpStatus.SC_OK, "Email sent successfully");
        } catch (SMTPSendFailedException sfe) {
            // SMTP 5xx errors indicate a permanent failure, while 4xx indicates a transient failure.
            // Since HTTP 5xx errors are retried by default while HTTP 4xx errors are not, map the codes accordingly.

            int replyCode = sfe.getReturnCode();
            // Permanent SMTP send failure, do not retry
            if (replyCode >= 500 && replyCode < 600) {
                throw new EmailSendingException(sfe, HttpStatus.SC_BAD_REQUEST);
            }

            // Transient SMTP send failure, retry may succeed
            throw new EmailSendingException(sfe, HttpStatus.SC_BAD_GATEWAY);
        } catch (MessagingException me) {
            throw new EmailSendingException(me, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (UnsupportedEncodingException uee) {
            throw new EmailSendingException(uee, HttpStatus.SC_BAD_REQUEST);
        }
    }

    MimeMessage parseToEmail(EmailWrapper wrapper) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);

        // Set sender, recipient, reply-to, and subject
        if (wrapper.getSenderName() == null || wrapper.getSenderName().isEmpty()) {
            message.setFrom(new InternetAddress(wrapper.getSenderEmail()));
        } else {
            message.setFrom(new InternetAddress(wrapper.getSenderEmail(), wrapper.getSenderName(), TEXT_ENCODING_UTF8));
        }
        message.setRecipient(RecipientType.TO, new InternetAddress(wrapper.getRecipient()));
        if (wrapper.getBcc() != null && !wrapper.getBcc().isEmpty()) {
            message.setRecipient(RecipientType.BCC, new InternetAddress(wrapper.getBcc()));
        }
        message.setReplyTo(new InternetAddress[] { new InternetAddress(wrapper.getReplyTo()) });
        message.setSubject(wrapper.getSubject(), TEXT_ENCODING_UTF8);

        // Set email content in text and HTML part
        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(HtmlHelper.htmlToPlainText(wrapper.getContent()), TEXT_ENCODING_UTF8);
        multipart.addBodyPart(textPart);
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(wrapper.getContent(), String.format("text/html; charset=%s", TEXT_ENCODING_UTF8));
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        return message;
    }

}
