package teammates.logic.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LogEvent;
import teammates.common.util.Logger;
import teammates.logic.core.EmailSenderService;
import teammates.logic.core.EmptyEmailService;
import teammates.logic.core.MailgunService;
import teammates.logic.core.MailjetService;
import teammates.logic.core.SendgridService;

/**
 * Handles operations related to sending emails.
 */
public class EmailSender {

    private static final Logger log = Logger.getLogger();

    private static final EmailSender instance = new EmailSender();
    private final EmailSenderService service;

    EmailSender() {
        if (Config.isDevServer()) {
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
        try {
            status = service.sendEmail(message);
        } catch (Exception e) {
            status = new EmailSendingStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (!status.isSuccess()) {
            log.severe("Email failed to send: " + status.getMessage());
        }

        Map<String, Object> emailDetailsPrivate = new HashMap<>();
        emailDetailsPrivate.put("emailRecipient", message.getRecipient());
        emailDetailsPrivate.put("emailSubject", message.getSubject());
        emailDetailsPrivate.put("emailContent", message.getContent());

        Map<String, Object> emailDetails = new HashMap<>();
        emailDetails.put("emailType", message.getType());
        emailDetails.put("emailDetails", emailDetailsPrivate);
        emailDetails.put("emailStatus", status.getStatusCode());
        if (status.getMessage() != null) {
            emailDetails.put("emailStatusMessage", status.getMessage());
        }
        log.event(LogEvent.EMAIL_SENT, "Email sent: " + message.getType(), emailDetails);

        return status;
    }

    private boolean isTestingAccount(String email) {
        return email.endsWith(Const.TEST_EMAIL_DOMAIN);
    }

    /**
     * Sends the given {@code report}.
     */
    public void sendReport(EmailWrapper report) {
        try {
            sendEmail(report);
        } catch (Exception e) {
            log.severe("Error in sending report: " + (report == null ? "" : report.getInfoForLogging())
                       + "\nReport content: " + (report == null ? "" : report.getContent())
                       + "\nCause: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
