package teammates.logic.email;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.logs.EmailSentLogDetails;
import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.external.email.EmailTransport;
import teammates.logic.external.email.EmptyEmailTransport;
import teammates.logic.external.email.MailjetTransport;
import teammates.logic.external.email.SendgridTransport;
import teammates.logic.external.email.SmtpTransport;

/**
 * Handles worker-side delivery of queued emails.
 */
public class EmailDeliveryService {

    private static final Logger log = Logger.getLogger();

    private static final EmailDeliveryService instance = new EmailDeliveryService();
    private final EmailTransport transport;

    EmailDeliveryService() {
        if (Config.isUsingSendgrid()) {
            transport = new SendgridTransport();
        } else if (Config.isUsingMailjet()) {
            transport = new MailjetTransport();
        } else if (Config.isUsingSmtp()) {
            transport = new SmtpTransport();
        } else {
            transport = new EmptyEmailTransport();
        }
    }

    EmailDeliveryService(EmailTransport transport) {
        this.transport = transport;
    }

    public static EmailDeliveryService inst() {
        return instance;
    }

    /**
     * Delivers the given queued email and generates a log report.
     *
     * @return The HTTP status of the email request.
     */
    public EmailSendingStatus deliver(EmailWrapper message) {
        if (!Config.isAllowSendingEmailsToTestDomain() && isTestingAccount(message.getRecipient())) {
            return new EmailSendingStatus(HttpStatus.SC_OK, "Not sending email to test account");
        }

        EmailSendingStatus status;
        EmailSendingException caughtE = null;
        try {
            status = transport.deliver(message);
        } catch (EmailSendingException e) {
            caughtE = e;
            status = new EmailSendingStatus(e.getStatusCode(), e.getMessage());
        }
        if (!status.isSuccess()) {
            if (caughtE == null) {
                log.severe("Email failed to send: " + status.getMessage());
            } else {
                log.severe("Email failed to send: " + status.getMessage(), caughtE);
            }
        }

        EmailSentLogDetails details = new EmailSentLogDetails();
        details.setEmailRecipient(message.getRecipient());
        details.setEmailSubject(message.getSubject());
        details.setEmailContent(message.getContent());
        details.setEmailType(message.getType());
        details.setEmailStatus(status.getStatusCode());

        if (status.getMessage() != null) {
            details.setEmailStatusMessage(status.getMessage());
        }
        log.event("Email sent: " + message.getType(), details);

        return status;
    }

    private boolean isTestingAccount(String email) {
        return email.endsWith(Const.TEST_EMAIL_DOMAIN);
    }

}
