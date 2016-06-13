package teammates.logic.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.Utils;

import com.google.appengine.labs.repackaged.org.json.JSONException;

/**
 * Handles operations related to sending e-mails.
 */
public class Emails {
    //TODO: methods in this class throw too many exceptions. Reduce using a wrapper exception?

    private static final Logger log = Utils.getLogger();
    
    public static String getEmailInfo(MimeMessage message)
            throws MessagingException {
        StringBuilder messageInfo = new StringBuilder(100);
        messageInfo.append("[Email sent]to=")
                   .append(message.getRecipients(Message.RecipientType.TO)[0]
                                .toString())
                   .append("|from=").append(message.getFrom()[0].toString())
                   .append("|subject=").append(message.getSubject());
        return messageInfo.toString();
    }
    
    public static String getEmailInfo(Sendgrid message) {
        StringBuilder messageInfo = new StringBuilder(100);
        messageInfo.append("[Email sent]to=").append(message.getTos().get(0))
                   .append("|from=").append(message.getFrom())
                   .append("|subject=").append(message.getSubject());
        return messageInfo.toString();
    }
    
    public void sendEmails(List<MimeMessage> messages) {
        if (messages.isEmpty()) {
            return;
        }
        
        // Equally spread out the emails to be sent over 1 hour
        int numberOfEmailsSent = 0;
        int emailIntervalMillis = (1000 * 60 * 60) / messages.size();

        // Sets interval to a maximum of 5 seconds if the interval is too large
        int maxIntervalMillis = 5000;
        emailIntervalMillis = emailIntervalMillis > maxIntervalMillis ? maxIntervalMillis : emailIntervalMillis;

        for (MimeMessage m : messages) {
            try {
                long emailDelayTimer = numberOfEmailsSent * emailIntervalMillis;
                addEmailToTaskQueue(m, emailDelayTimer);
                numberOfEmailsSent++;
            } catch (MessagingException e) {
                logSevereForErrorInSendingItem("message", m, e);
            }
        }

    }

    private void addEmailToTaskQueue(MimeMessage message, long emailDelayTimer) throws MessagingException {
        String emailSubject = message.getSubject();
        String emailSender = message.getFrom()[0].toString();
        String emailReceiver = message.getRecipients(Message.RecipientType.TO)[0].toString();
        String emailReplyToAddress = message.getReplyTo()[0].toString();
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.EMAIL_SUBJECT, emailSubject);
            paramMap.put(ParamsNames.EMAIL_CONTENT, message.getContent().toString());
            paramMap.put(ParamsNames.EMAIL_SENDER, emailSender);
            paramMap.put(ParamsNames.EMAIL_RECEIVER, emailReceiver);
            paramMap.put(ParamsNames.EMAIL_REPLY_TO_ADDRESS, emailReplyToAddress);
            
            TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
            taskQueueLogic.createAndAddDeferredTask(SystemParams.SEND_EMAIL_TASK_QUEUE,
                    Const.ActionURIs.SEND_EMAIL_WORKER, paramMap, emailDelayTimer);
        } catch (Exception e) {
            log.severe("Error when adding email to task queue: " + e.getMessage() + "\n"
                       + "Email sender: " + emailSender + "\n"
                       + "Email receiver: " + emailReceiver + "\n"
                       + "Email subject: " + emailSubject + "\n"
                       + "Email reply to address: " + emailReplyToAddress);
        }
        
    }
    
    public void sendEmailWithLogging(MimeMessage message) throws MessagingException, JSONException, IOException {
        sendEmail(message, true);
    }
    
    public void sendEmailWithoutLogging(MimeMessage message) throws MessagingException, JSONException, IOException {
        sendEmail(message, false);
    }
    
    /**
     * Sends email through GAE irrespective of config properties
     * Does not generate log report
     * @param message
     * @throws MessagingException
     */
    public void forceSendEmailThroughGaeWithoutLogging(MimeMessage message) throws MessagingException {
        sendUsingGae(message);
    }
    
    /**
     * Sends email through GAE irrespective of config properties
     * Generates log report
     * @param message
     * @throws MessagingException
     */
    public void forceSendEmailThroughGaeWithLogging(MimeMessage message) throws MessagingException {
        sendUsingGae(message);
        generateLogReport(message);
    }

    /**
     * This method sends the email and has an option to log its receiver, subject and content
     * @param message
     * @param isWithLogging
     * @throws MessagingException
     * @throws IOException
     * @throws JSONException
     */
    private void sendEmail(MimeMessage message, boolean isWithLogging) throws MessagingException, JSONException, IOException {
        if (Config.isUsingSendgrid()) {
            sendUsingSendgrid(message);
            
            if (isWithLogging) {
                generateLogReport(parseMimeMessageToSendgrid(message));
            }
        } else {
            sendUsingGae(message);
            
            if (isWithLogging) {
                generateLogReport(message);
            }
        }
    }
    
    private void sendUsingGae(MimeMessage message) throws MessagingException {
        log.info(getEmailInfo(message));
        Transport.send(message);
    }

    private void sendUsingSendgrid(MimeMessage message) throws MessagingException, JSONException, IOException {
        Sendgrid email = parseMimeMessageToSendgrid(message);
        log.info(getEmailInfo(email));
        
        try {
            email.send();
        } catch (Exception e) {
            log.severe("Sendgrid failed, sending with GAE mail");
            Transport.send(message);
        }
    }
    
    private void generateLogReport(Sendgrid message) {
        try {
            EmailLogEntry newEntry = new EmailLogEntry(message);
            String emailLogInfo = newEntry.generateLogMessage();
            log.info(emailLogInfo);
        } catch (Exception e) {
            log.severe("Failed to generate log for email: " + getEmailInfo(message));
        }
    }
    
    private void generateLogReport(MimeMessage message) throws MessagingException {
        try {
            EmailLogEntry newEntry = new EmailLogEntry(message);
            String emailLogInfo = newEntry.generateLogMessage();
            log.info(emailLogInfo);
        } catch (Exception e) {
            log.severe("Failed to generate log for email: " + getEmailInfo(message));
        }
    }
    
    public void sendErrorReport(MimeMessage errorReport) throws MessagingException {
        forceSendEmailThroughGaeWithoutLogging(errorReport);
        log.info("Sent crash report: " + Emails.getEmailInfo(errorReport));
    }
    
    public void sendBackupErrorReport(MimeMessage errorReport, Throwable error, Exception e) {
        log.severe("Crash report failed to send. Detailed error stack trace: "
                   + TeammatesException.toStringWithStackTrace(error));
        logSevereForErrorInSendingItem("crash report", errorReport, e);
    }

    public MimeMessage sendLogReport(MimeMessage message) {
        try {
            forceSendEmailThroughGaeWithoutLogging(message);
        } catch (Exception e) {
            logSevereForErrorInSendingItem("log report", message, e);
        }
        return message;
    }
    
    private void logSevereForErrorInSendingItem(String itemType, MimeMessage message, Exception e) {
        log.severe("Error in sending " + itemType + ": " + (message == null ? "" : message.toString())
                   + "\nCause: " + TeammatesException.toStringWithStackTrace(e));
    }
    
    public Sendgrid parseMimeMessageToSendgrid(MimeMessage message) throws MessagingException, JSONException, IOException {
        Sendgrid email = new Sendgrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);
        
        for (int i = 0; i < message.getRecipients(Message.RecipientType.TO).length; i++) {
            email.addTo(message.getRecipients(Message.RecipientType.TO)[i].toString());
        }
        
        String from = extractSenderEmail(message.getFrom()[0].toString());
        String html = message.getContent().toString();
        
        email.setFrom(from)
             .setSubject(message.getSubject())
             .setHtml(html)
             .setText(Jsoup.parse(html).text());
        
        if (message.getRecipients(Message.RecipientType.BCC) != null
                                        && message.getRecipients(Message.RecipientType.BCC).length > 0) {
            email.setBcc(message.getRecipients(Message.RecipientType.BCC)[0].toString());
        }
        
        if (message.getReplyTo() != null && message.getReplyTo().length > 0) {
            email.setReplyTo(message.getReplyTo()[0].toString());
        }
        
        return email;
    }

    /**
     * Extracts sender email from the string with name and email in the format: Name <Email>
     * @param from String with sender information in the format: Name <Email>
     * @return Sender email
     */
    public String extractSenderEmail(String from) {
        if (from.contains("<") && from.contains(">")) {
            return from.substring(from.indexOf('<') + 1, from.indexOf('>'));
        }
        return from;
    }
}
