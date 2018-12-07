package teammates.logic.api;

import java.util.List;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.core.EmailSenderService;
import teammates.logic.core.JavamailService;
import teammates.logic.core.MailgunService;
import teammates.logic.core.MailjetService;
import teammates.logic.core.SendgridService;

/**
 * Handles operations related to sending emails.
 */
public class EmailSender {

    private static final Logger log = Logger.getLogger();

    private final EmailSenderService service;

    public EmailSender() {
        if (Config.isUsingSendgrid()) {
            service = new SendgridService();
        } else if (Config.isUsingMailgun()) {
            service = new MailgunService();
        } else if (Config.isUsingMailjet()) {
            service = new MailjetService();
        } else {
            service = new JavamailService();
        }
    }

    /**
     * Sends the given {@code message} and generates a log report.
     */
    public void sendEmail(EmailWrapper message) throws EmailSendingException {
        service.sendEmail(message);

        EmailLogEntry newEntry = new EmailLogEntry(message);
        String emailLogInfo = newEntry.generateLogMessage();
        log.info(emailLogInfo);
    }

    /**
     * Sends the given {@code message} with Javamail service regardless of configuration.
     */
    private void sendEmailCopyWithJavamail(EmailWrapper message) throws EmailSendingException {
        // GAE Javamail is used when we need a service that is not prone to configuration failures
        // and/or third-party API failures. The trade-off is the very little quota of 100 emails per day.
        JavamailService javamailService = new JavamailService();

        // GAE Javamail requires the sender email address to be of this format
        message.setSenderEmail("admin@" + Config.getAppId() + ".appspotmail.com");

        message.setSubject("[Javamail Copy] " + message.getSubject());

        javamailService.sendEmail(message);
    }

    /**
     * Sends the given {@code report}.
     */
    public void sendReport(EmailWrapper report) {
        try {
            sendEmail(report);
            sendEmailCopyWithJavamail(report);
        } catch (Exception e) {
            log.severe("Error in sending report: " + (report == null ? "" : report.getInfoForLogging())
                       + "\nReport content: " + (report == null ? "" : report.getContent())
                       + "\nCause: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Gets the emails sent.
     * This method is used only for testing, where it is overridden.
     *
     * @throws UnsupportedOperationException if used in production, where it is not meant to be
     */
    public List<EmailWrapper> getEmailsSent() {
        throw new UnsupportedOperationException("Method is used only for testing");
    }

}
