package teammates.logic.automated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.logic.core.Emails;

public abstract class EmailAction {

    protected static final Logger log = Utils.getLogger();
    
    protected HttpServletRequest req;
    protected List<MimeMessage> emailsToBeSent;

    protected String actionName = "unspecified";
    protected String actionDescription = "unspecified";
    
    protected Boolean isError = false;
    
    public EmailAction() {
        req = null;
        emailsToBeSent = null;
    }
    
    public EmailAction(HttpServletRequest request) {
        
        req = request;
        emailsToBeSent = null;
    }

    public void sendEmails() {
        try {
            emailsToBeSent = prepareMailToBeSent();
            
            //actually send the mail
            Emails emailManager = new Emails();
            emailManager.sendEmails(emailsToBeSent);
            doPostProcessingForSuccesfulSend();
            
            //carry this out if mail is successfully sent
            ArrayList<MimeMessage> emailList = new ArrayList<MimeMessage>();
            emailList.addAll(emailsToBeSent);
            logActivitySuccess(req, emailList);
                
        } catch (Exception e) {
            isError = true;
            logActivityFailure(req, e);
            log.severe("Unexpected error " + TeammatesException.toStringWithStackTrace(e));
        } finally {
            if (isError) {
                try {
                    doPostProcessingForUnsuccesfulSend();
                } catch (EntityDoesNotExistException e) {
                    logActivityFailure(req, e);
                    log.severe("Unexpected error " + TeammatesException.toStringWithStackTrace(e));
                }
            }
        }
    }

    /*
     *  Used for testing
     */
    public List<MimeMessage> getPreparedEmailsAndPerformSuccessOperations() {
        List<MimeMessage> preparedMail = null;
        
        try {
            preparedMail = prepareMailToBeSent();
            doPostProcessingForSuccesfulSend();
        } catch (Exception e) {
            log.severe("Unexpected error " + TeammatesException.toStringWithStackTrace(e));
        }
        return preparedMail;
    }
    
    protected abstract void doPostProcessingForSuccesfulSend() throws InvalidParametersException, EntityDoesNotExistException;
    
    protected abstract void doPostProcessingForUnsuccesfulSend() throws EntityDoesNotExistException;
    
    protected abstract List<MimeMessage> prepareMailToBeSent() throws MessagingException, IOException, EntityDoesNotExistException;
    
    protected void logActivitySuccess(HttpServletRequest req, ArrayList<MimeMessage> emails) {
        String url = HttpRequestHelper.getRequestedUrl(req);
        String message;
        
        try {
            message = generateLogMessage(emails);
        } catch (Exception e) {
            message = "<span class=\"color_red\">Unable to retrieve email targets in "
                            + actionName + ": " + actionDescription + ".</span>";
        }
        
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(actionName, actionDescription, null, message, url);
        log.info(activityLogEntry.generateLogMessage());
    }

    protected void logActivityFailure(HttpServletRequest req, Throwable e) {
                
        String url = HttpRequestHelper.getRequestedUrl(req);
    
        String message = "<span class=\"color_red\">Servlet Action failure in " + actionName + "<br>"
                       + e.getMessage() + "</span>";
        ActivityLogEntry activityLogEntry = new ActivityLogEntry(actionName, actionDescription, null,
                                                                 message, url);
        log.info(activityLogEntry.generateLogMessage());
        log.severe(e.getMessage());
    }

    private String generateLogMessage(List<MimeMessage> emailsSent) throws MessagingException, IOException {
        StringBuilder logMessage = new StringBuilder(100);
        logMessage.append("Emails sent to:<br/>");
        
        Iterator<Entry<String, EmailData>> extractedEmailIterator =
                extractEmailDataForLogging(emailsSent).entrySet().iterator();
        
        while (extractedEmailIterator.hasNext()) {
            Entry<String, EmailData> extractedEmail = extractedEmailIterator.next();
            
            String userEmail = extractedEmail.getKey();
            EmailData emailData = extractedEmail.getValue();
            
            logMessage.append(emailData.userName + "<span class=\"bold\"> (" + userEmail + ")</span>.<br/>");
            if (!emailData.regKey.isEmpty()) {
                logMessage.append(emailData.regKey).append("<br/>");
            }
        }
        
        return logMessage.toString();
    }
    
    private Map<String, EmailData> extractEmailDataForLogging(List<MimeMessage> emails)
            throws MessagingException, IOException {
        Map<String, EmailData> logData = new TreeMap<String, EmailData>();
        
        for (MimeMessage email : emails) {
            String recipient = email.getAllRecipients()[0].toString();
            String userName = extractUserName((String) email.getContent());
            String regKey = extractRegistrationKey((String) email.getContent());
            logData.put(recipient, new EmailData(userName, regKey));
        }
        
        return logData;
    }
    
    private String extractUserName(String emailContent) {
        int startIndex = emailContent.indexOf("Hello ") + "Hello ".length();
        int endIndex = emailContent.indexOf(',');
        return emailContent.substring(startIndex, endIndex);
    }
    
    private String extractRegistrationKey(String emailContent) {
        if (emailContent.contains("key=")) {
            int startIndex = emailContent.indexOf("key=") + "key=".length();
            int endIndex = emailContent.indexOf("\">http://");
            return emailContent.substring(startIndex, endIndex);
        }
        return "";
    }
    
    private class EmailData {
        String userName;
        String regKey;
        
        EmailData(String studentName, String regKey) {
            this.userName = studentName;
            this.regKey = regKey;
        }
    }
}
