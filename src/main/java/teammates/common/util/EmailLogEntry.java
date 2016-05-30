package teammates.common.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.logic.core.Sendgrid;

import com.google.appengine.api.log.AppLogLine;

/** A log entry which contains info about subject, receiver, content and sent date of a sent email*/
public class EmailLogEntry {
    
    private String receiver;
    private String subject;
    private String content;
    private long time;
    
    @SuppressWarnings("unused") // used by js
    private String logInfoAsHtml;
    
    public EmailLogEntry(MimeMessage msg) throws MessagingException, IOException {
        this.receiver = msg.getRecipients(Message.RecipientType.TO)[0].toString();
        this.subject = msg.getSubject();
        this.content = (String) msg.getContent();
    }
    
    public EmailLogEntry(Sendgrid msg) {
        
        this.receiver = msg.getTos().get(0);
        this.subject = msg.getSubject();
        this.content = msg.getHtml();
    }
    
    public EmailLogEntry(AppLogLine appLog) {
        this.time = appLog.getTimeUsec() / 1000;
        String[] tokens = appLog.getLogMessage().split("\\|\\|\\|", -1);
        
        try {
            this.receiver = tokens[1];
            this.subject = tokens[2];
            this.content = tokens[3];
            logInfoAsHtml = getLogInfoForTableRowAsHtml();
        } catch (ArrayIndexOutOfBoundsException e) {
            this.receiver = "";
            this.subject = "";
            this.content = "";
            logInfoAsHtml = "";
        }
    }
    
    private String getLogInfoForTableRowAsHtml() {
        return String.format(
                "<tr class=\"log\"><td>%s</td><td>%s</td><td>%s</td></tr>"
                + "<tr id=\"small\"><td colspan=\"3\"><ul class=\"list-group\">"
                + "<li class=\"list-group-item list-group-item-info\">,"
                + "<input type=\"text\" value=\"%s\" class=\"form-control\" readonly></li>"
                + "</ul></td></tr>"
                + "<tr id=\"big\" style=\"display:none;\">"
                + "<td colspan=\"3\"><div class=\"well\"><ul class=\"list-group\"><li class=\"list-group-item list-group-item-success\">"
                + "<small>%s</small>"
                + "</li></ul></div></td>"
                + "</tr>",
                this.receiver, this.subject, this.getTimeForDisplay(), this.getContent(), this.content);
    }
    
    /**
     * Generates a log message that will be logged in the server
     */
    public String generateLogMessage() {
        //TEAMMATESEMAILSLOG|||RECEIVER|||SUBJECT|||CONTENT
        return "TEAMMATESEMAILLOG|||" + this.receiver + "|||" + this.subject + "|||" + this.content;
        
    }
    
    public String getReceiver() {
        return this.receiver;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public String getContent() {
        return Sanitizer.sanitizeForHtml(this.content);
    }
    
    public String getUnsanitizedContent() {
        return StringHelper.recoverFromSanitizedText(content);
    }
    
    public String getTimeForDisplay() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        appCal.setTimeInMillis(time);
        appCal = TimeHelper.convertToUserTimeZone(appCal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        return TimeHelper.formatTime12H(appCal.getTime());
    }
    
    public void highlightKeyStringInMessageInfoHtml(String[] keyStringsToHighlight, String part) {
        if (keyStringsToHighlight == null) {
            return;
        }
        
        if (part.contains("receiver")) {
            this.receiver = hightlightTextWithKeyWords(keyStringsToHighlight, this.receiver);
        }
        
        if (part.contains("subject")) {
            this.subject = hightlightTextWithKeyWords(keyStringsToHighlight, this.subject);
        }
        
        if (part.contains("content")) {
            this.content = hightlightTextWithKeyWords(keyStringsToHighlight, this.content);
        }
        
        logInfoAsHtml = getLogInfoForTableRowAsHtml();
    }
    
    private String hightlightTextWithKeyWords(String[] keyStringsToHighlight, String text) {
        if (text == null) {
            return text;
        }
        String highlightedText = text;
        for (String stringToHighlight : keyStringsToHighlight) {
            if (highlightedText.toLowerCase().contains(stringToHighlight.toLowerCase())) {
                
                int startIndex = highlightedText.toLowerCase().indexOf(stringToHighlight.toLowerCase());
                int endIndex = startIndex + stringToHighlight.length();
                String realStringToHighlight = highlightedText.substring(startIndex, endIndex);
                highlightedText = highlightedText.replace(realStringToHighlight, "<mark>" + realStringToHighlight + "</mark>");
            }
        }
        
        return highlightedText;
    }
}
