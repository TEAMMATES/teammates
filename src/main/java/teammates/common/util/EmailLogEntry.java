package teammates.common.util;

import java.util.Calendar;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import teammates.googleSendgridJava.Sendgrid;

import com.google.appengine.api.log.AppLogLine;

/** A log entry which contains info about subject, receiver, content and sent date of a sent email*/
public class EmailLogEntry {
    
    private String receiver;
    private String subject;
    private String content;
    private long time;
    
    public String logInfoAsHtml;
    
    public EmailLogEntry(MimeMessage msg) throws Exception{
        
            this.receiver = msg.getRecipients(Message.RecipientType.TO)[0].toString();
            this.subject = msg.getSubject();
            this.content = (String) msg.getContent(); 
    }
    
    public EmailLogEntry(Sendgrid msg) throws Exception{
        
        this.receiver = msg.getTos().get(0);
        this.subject = msg.getSubject();
        this.content = msg.getHtml(); 
    }
    
    public EmailLogEntry(AppLogLine appLog){
        this.time = appLog.getTimeUsec() / 1000;
        String[] tokens = appLog.getLogMessage().split("\\|\\|\\|", -1);
        
        try{
            this.receiver = tokens[1];
            this.subject = tokens[2];
            this.content = tokens[3];
            logInfoAsHtml = getLogInfoForTableRowAsHtml();
        } catch (ArrayIndexOutOfBoundsException e){
            this.receiver = "";
            this.subject = "";
            this.content = "";
            logInfoAsHtml = "";
        }
    }
    
    private String getLogInfoForTableRowAsHtml(){

        StringBuilder str = new StringBuilder();
        str.append("<tr class=\"log\">");
        str.append("<td>" + this.receiver + "</td>");
        str.append("<td>" + this.subject + "</td>");
        str.append("<td>" + this.getTimeForDisplay() + "</td>");
        str.append("</tr>");
        str.append("<tr id=\"small\">");
        str.append("<td colspan=\"3\">");
        str.append("<ul class=\"list-group\">");
        str.append("<li class=\"list-group-item list-group-item-info\">");
        str.append("<input type=\"text\" value=\"" + this.getContent() + "\" class=\"form-control\"");
        str.append(" readonly=\"readonly\">");
        str.append("</li>");
        str.append("</ul>    ");
        str.append("</td>");
        str.append("</tr>");
        str.append("<tr id=\"big\" style=\"display:none;\">");
        str.append("<td colspan=\"3\">");
        str.append("<div class=\"well\">");
        str.append("<ul class=\"list-group\">");
        str.append("<li class=\"list-group-item list-group-item-success\"><small>");
        str.append(this.content + "</small>");
        str.append("</li>");
        str.append("</ul>");
        str.append("</div>");
        str.append("</td>");
        str.append("</tr>");
        
        return str.toString();
    }
    
    /**
     * Generates a log message that will be logged in the server
     */
    public String generateLogMessage(){
        //TEAMMATESEMAILSLOG|||RECEIVER|||SUBJECT|||CONTENT
        return "TEAMMATESEMAILLOG|||" + this.receiver + "|||" + 
               this.subject + "|||" + this.content;
        
    }
    
    public String getReceiver(){
        return this.receiver;
    }
    
    public String getSubject(){
        return this.subject;
    }
    
    public long getTime(){
        return this.time;
    }
    
    public String getContent(){
        return Sanitizer.sanitizeForHtml(this.content);
    }
    
    public String getUnsanitizedContent() {
        return StringHelper.recoverFromSanitizedText(content);
    }
    
    public String getTimeForDisplay(){
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        appCal.setTimeInMillis(time);
        appCal = TimeHelper.convertToUserTimeZone(appCal, Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
        return TimeHelper.formatTime(appCal.getTime());
    }
    
    public void highlightKeyStringInMessageInfoHtml(String[] keyStringsToHighlight, String part){
        if(keyStringsToHighlight == null){
            return;
        }
        
        if(part.contains("receiver")){
            this.receiver = hightlightTextWithKeyWords(keyStringsToHighlight, this.receiver);
        }
        
        if(part.contains("subject")){
            this.subject = hightlightTextWithKeyWords(keyStringsToHighlight, this.subject);
        }
        
        if(part.contains("content")){
            this.content = hightlightTextWithKeyWords(keyStringsToHighlight, this.content);
        }
        
        logInfoAsHtml = getLogInfoForTableRowAsHtml();
    }
    
    private String hightlightTextWithKeyWords(String[] keyStringsToHighlight, String text){
        if(text == null){
            return text;
        }
        
        for(String stringToHighlight : keyStringsToHighlight){
            if(text.toLowerCase().contains(stringToHighlight.toLowerCase())){
                
                int startIndex = text.toLowerCase().indexOf(stringToHighlight.toLowerCase());
                int endIndex = startIndex + stringToHighlight.length();                         
                String realStringToHighlight = text.substring(startIndex, endIndex);               
                text = text.replace(realStringToHighlight, "<mark>" + realStringToHighlight + "</mark>");
            }
        }
        
        return text;
    }
}
