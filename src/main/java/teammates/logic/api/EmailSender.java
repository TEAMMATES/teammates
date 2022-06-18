package teammates.logic.api;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.logs.EmailSentLogDetails;
import teammates.common.exception.EmailSendingException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.external.EmailSenderService;
import teammates.logic.external.EmptyEmailService;
import teammates.logic.external.MailgunService;
import teammates.logic.external.MailjetService;
import teammates.logic.external.SendgridService;

/**
 * Handles operations related to sending emails.
 */
public class EmailSender {

    private static final Logger log = Logger.getLogger();

    private static final EmailSender instance = new EmailSender();
    private final EmailSenderService service;

    EmailSender() {
        if (Config.IS_DEV_SERVER) {
            service = new EmptyEmailService();
        } else {
            if (Config.isUsingSendgrid()) {
                service = new SendgridService();
            } else if (Config.isUsingMailgun()) {
                service = new MailgunService();
            } else if (Config.isUsingMailjet()) {
                service = new MailjetService();
            } else {
                service = new EmptyEmailService();
            }
        }
    }

    public static EmailSender inst() {
        return instance;
    }

    /**
     * Sends the given {@code message} and generates a log report.
     *
     * @return The HTTP status of the email request.
     */
    public EmailSendingStatus sendEmail(EmailWrapper message) {
        if (isTestingAccount(message.getRecipient())) {
            return new EmailSendingStatus(HttpStatus.SC_OK, "Not sending email to test account");
        }

        EmailSendingStatus status;
        EmailSendingException caughtE = null;
        try {
            status = service.sendEmail(message);
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
