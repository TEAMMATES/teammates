package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

public class EmailWrapper {
    
    private String senderName;
    private String senderEmail;
    private String replyTo;
    private List<String> recipientsList = new ArrayList<String>();
    private List<String> bccList = new ArrayList<String>();
    private String subject;
    private String content;
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderEmail() {
        return senderEmail;
    }
    
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    
    public String getReplyTo() {
        return replyTo;
    }
    
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
    
    public List<String> getRecipientsList() {
        return recipientsList;
    }
    
    public String getFirstRecipient() {
        return getRecipientsList().get(0);
    }
    
    public void addRecipient(String recipient) {
        if (!recipientsList.contains(recipient)) {
            recipientsList.add(recipient);
        }
    }
    
    public List<String> getBccList() {
        return bccList;
    }
    
    public void addBcc(String bcc) {
        if (!bccList.contains(bcc)) {
            bccList.add(bcc);
        }
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getInfoForLogging() {
        return "[Email sent]to=" + getFirstRecipient()
               + "|from=" + getSenderEmail()
               + "|subject=" + getSubject();
    }
    
}
