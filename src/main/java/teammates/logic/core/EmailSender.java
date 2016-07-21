package teammates.logic.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Utils;

/**
 * Handles operations related to sending emails.
 */
public class EmailSender {
    
    private static final Logger log = Utils.getLogger();
    
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
     * Sends the given list of {@code messages}.
     */
    public void sendEmails(List<EmailWrapper> messages) {
        if (messages.isEmpty()) {
            return;
        }
        
        // Equally spread out the emails to be sent over 1 hour
        // Sets interval to a maximum of 5 seconds if the interval is too large
        int oneHourInMillis = 60 * 60 * 1000;
        int emailIntervalMillis = Math.min(5000, oneHourInMillis / messages.size());
        
        int numberOfEmailsSent = 0;
        for (EmailWrapper m : messages) {
            long emailDelayTimer = numberOfEmailsSent * emailIntervalMillis;
            addEmailToTaskQueue(m, emailDelayTimer);
            numberOfEmailsSent++;
        }
    }
    
    private void addEmailToTaskQueue(EmailWrapper message, long emailDelayTimer) {
        String emailSubject = message.getSubject();
        String emailSenderName = message.getSenderName();
        String emailSender = message.getSenderEmail();
        String emailReceiver = message.getRecipient();
        String emailReplyToAddress = message.getReplyTo();
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.EMAIL_SUBJECT, emailSubject);
            paramMap.put(ParamsNames.EMAIL_CONTENT, message.getContent());
            paramMap.put(ParamsNames.EMAIL_SENDER, emailSender);
            if (emailSenderName != null && !emailSenderName.isEmpty()) {
                paramMap.put(ParamsNames.EMAIL_SENDERNAME, emailSenderName);
            }
            paramMap.put(ParamsNames.EMAIL_RECEIVER, emailReceiver);
            paramMap.put(ParamsNames.EMAIL_REPLY_TO_ADDRESS, emailReplyToAddress);
            
            TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
            taskQueueLogic.createAndAddDeferredTask(SystemParams.SEND_EMAIL_TASK_QUEUE,
                    Const.ActionURIs.SEND_EMAIL_WORKER, paramMap, emailDelayTimer);
        } catch (Exception e) {
            log.severe("Error when adding email to task queue: " + e.getMessage() + "\n"
                       + "Email sender: " + emailSender + "\n"
                       + "Email sender name: " + emailSenderName + "\n"
                       + "Email receiver: " + emailReceiver + "\n"
                       + "Email subject: " + emailSubject + "\n"
                       + "Email reply to address: " + emailReplyToAddress);
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
     * Sends the given {@code errorReport}.
     */
    public void sendErrorReport(EmailWrapper errorReport) throws EmailSendingException {
        sendEmail(errorReport);
        sendEmailCopyWithJavamail(errorReport);
        log.info("Sent crash report: " + errorReport.getInfoForLogging());
    }
    
    /**
     * Reports that a system {@code error} has occurred and the {@code errorReport} that is
     * supposed to report it has failed to sent.<br>
     * This method can be used when the usual error report sending fails to make sure that
     * no stack traces are lost in the process.
     * @param error the original error to be reported in {@code errorReport}
     * @param errorReport the report that fails to send
     * @param e the exception which causes {@code errorReport} to fail to send
     */
    public void reportErrorThroughFallbackChannel(Throwable error, EmailWrapper errorReport, Exception e) {
        log.severe("Crash report failed to send. Detailed error stack trace: "
                   + TeammatesException.toStringWithStackTrace(error));
        logSevereForErrorInSendingItem("crash report", errorReport, e);
    }
    
    /**
     * Sends the given {@code logReport}.
     */
    public void sendLogReport(EmailWrapper logReport) {
        try {
            sendEmail(logReport);
            sendEmailCopyWithJavamail(logReport);
        } catch (Exception e) {
            logSevereForErrorInSendingItem("log report", logReport, e);
        }
    }
    
    private void logSevereForErrorInSendingItem(String itemType, EmailWrapper message, Exception e) {
        log.severe("Error in sending " + itemType + ": " + (message == null ? "" : message.getInfoForLogging())
                   + "\nCause: " + TeammatesException.toStringWithStackTrace(e));
    }
    
}
